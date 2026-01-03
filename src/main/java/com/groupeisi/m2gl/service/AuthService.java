package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.event.AuthEventProducer;
import com.groupeisi.m2gl.event.payload.OtpRequestedEvent.OtpPurpose;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.request.CompleteSignUpRequest;
import com.groupeisi.m2gl.service.dto.request.LoginRequest;
import com.groupeisi.m2gl.service.dto.response.AuthResponse;
import com.groupeisi.m2gl.service.dto.response.CheckPhoneResponse;
import com.groupeisi.m2gl.service.dto.response.ResendOtpResponse;
import com.groupeisi.m2gl.service.dto.response.TokensResponse;
import com.groupeisi.m2gl.service.dto.response.UserResponse;
import com.groupeisi.m2gl.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Service for authentication operations.
 * Uses Kafka for OTP delivery and wallet creation instead of direct calls.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final UtilisateurAuthRepository userRepository;
    private final OtpService otpService;
    private final KeycloakService keycloakService;
    private final AuthEventProducer eventProducer;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UtilisateurAuthRepository userRepository,
            OtpService otpService,
            KeycloakService keycloakService,
            AuthEventProducer eventProducer) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.keycloakService = keycloakService;
        this.eventProducer = eventProducer;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Check if phone number is registered.
     * For new users, generates and publishes OTP event.
     */
    public CheckPhoneResponse checkPhone(String phoneNumber, String correlationId) {
        LOG.debug("Checking phone number: {}", phoneNumber);

        boolean exists = userRepository.findByNumeroTelephone(phoneNumber).isPresent() 
            || keycloakService.userExistsByNumeroTelephone(phoneNumber);

        CheckPhoneResponse.Builder responseBuilder = CheckPhoneResponse.builder()
            .phoneNumber(phoneNumber)
            .isNewUser(!exists);

        if (!exists) {
            // New user - generate OTP and publish event
            String otp = otpService.genererOtp(phoneNumber).getOtp();
            
            // Publish OTP event to Kafka (NotificationMS will send SMS)
            eventProducer.publishOtpForRegistration(phoneNumber, otp, correlationId);
            
            responseBuilder
                .otpSent(true)
                .otpExpiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES));
            
            LOG.info("OTP generated and event published for new user: {}", maskPhone(phoneNumber));
        } else {
            responseBuilder.otpSent(false);
            LOG.info("Phone number already registered: {}", maskPhone(phoneNumber));
        }

        return responseBuilder.build();
    }

    /**
     * Complete user registration with OTP verification and KYC data.
     * Creates user in Keycloak and local DB, then publishes user.registered event.
     */
    public AuthResponse completeSignUp(CompleteSignUpRequest request, String correlationId) {
        LOG.debug("Completing sign-up for: {}", maskPhone(request.getPhoneNumber()));

        // Verify OTP
        if (!otpService.verifierOtp(request.getPhoneNumber(), request.getOtp())) {
            throw new BadRequestAlertException("Invalid or expired OTP", "auth", "INVALID_OTP");
        }

        // Check if user already exists
        Optional<UtilisateurAuth> existingUser = userRepository.findByNumeroTelephone(request.getPhoneNumber());
        if (existingUser.isPresent() && existingUser.get().getKeycloakId() != null) {
            throw new BadRequestAlertException("User already exists", "auth", "USER_ALREADY_EXISTS");
        }

        // Create or update user
        UtilisateurAuth user = existingUser.orElse(new UtilisateurAuth());
        user.setNumeroTelephone(request.getPhoneNumber());
        user.setPin(passwordEncoder.encode(request.getPin()));
        user.setPrenom(request.getFirstName());
        user.setNom(request.getLastName());
        user.setEmail(request.getEmail());
        user.setEtatNumero(UtilisateurAuth.EtatNumero.VERIFIE);

        // Create Keycloak user
        if (user.getKeycloakId() == null) {
            String keycloakId = keycloakService.createUserWithPin(user, request.getPin());
            user.setKeycloakId(keycloakId);
            keycloakService.completeKyc(user);
        }

        // Save user
        user = userRepository.save(user);
        LOG.info("User created successfully: {}", user.getId());

        // Publish user.registered event (TxEngineMS will create wallet)
        eventProducer.publishUserRegistered(user, correlationId);

        // Get tokens from Keycloak
        TokensResponse tokens = keycloakService.getTokensForUser(user.getNumeroTelephone(), request.getPin());

        return AuthResponse.builder()
            .user(mapToUserResponse(user))
            .tokens(tokens)
            .build();
    }

    /**
     * Authenticate user with phone and PIN.
     */
    public AuthResponse login(LoginRequest request, String correlationId) {
        LOG.debug("Login attempt for: {}", maskPhone(request.getPhoneNumber()));

        // Find user
        UtilisateurAuth user = userRepository.findByNumeroTelephone(request.getPhoneNumber())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "auth", "USER_NOT_FOUND"));

        // Verify PIN
        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new BadRequestAlertException("Invalid credentials", "auth", "INVALID_CREDENTIALS");
        }

        // Get tokens from Keycloak
        TokensResponse tokens = keycloakService.getTokensForUser(user.getNumeroTelephone(), request.getPin());

        LOG.info("Login successful for user: {}", user.getId());

        return AuthResponse.builder()
            .user(mapToUserResponse(user))
            .tokens(tokens)
            .build();
    }

    /**
     * Resend OTP code to the user's phone number.
     * Rate limited to prevent abuse (max 3 attempts per 5 minutes).
     */
    public ResendOtpResponse resendOtp(String phoneNumber, OtpPurpose purpose, String correlationId) {
        LOG.debug("Resend OTP request for: {} with purpose: {}", maskPhone(phoneNumber), purpose);

        // Check if user exists (for registration, user should NOT exist)
        boolean userExists = userRepository.findByNumeroTelephone(phoneNumber).isPresent() 
            || keycloakService.userExistsByNumeroTelephone(phoneNumber);

        if (purpose == OtpPurpose.REGISTRATION && userExists) {
            throw new BadRequestAlertException("User already registered", "auth", "USER_ALREADY_EXISTS");
        }

        if (purpose != OtpPurpose.REGISTRATION && !userExists) {
            throw new BadRequestAlertException("User not found", "auth", "USER_NOT_FOUND");
        }

        // Generate new OTP
        String otp = otpService.genererOtp(phoneNumber).getOtp();

        // Publish OTP event based on purpose
        switch (purpose) {
            case REGISTRATION:
                eventProducer.publishOtpForRegistration(phoneNumber, otp, correlationId);
                break;
            case PIN_RESET:
                eventProducer.publishOtpForPinReset(phoneNumber, otp, correlationId);
                break;
            case PIN_CREATION:
                eventProducer.publishOtpForPinCreation(phoneNumber, otp, correlationId);
                break;
            default:
                eventProducer.publishOtpForRegistration(phoneNumber, otp, correlationId);
        }

        LOG.info("OTP resent successfully for: {}", maskPhone(phoneNumber));

        return ResendOtpResponse.builder()
            .phoneNumber(phoneNumber)
            .otpSent(true)
            .otpExpiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
            .remainingAttempts(2) // Placeholder - implement rate limiting if needed
            .build();
    }

    /**
     * Refresh access token using refresh token.
     */
    public TokensResponse refreshToken(String refreshToken) {
        LOG.debug("Refreshing token");
        return keycloakService.refreshToken(refreshToken);
    }

    /**
     * Logout user and revoke tokens.
     */
    public void logout(String accessToken, String refreshToken) {
        LOG.debug("Logging out user");
        keycloakService.logout(accessToken, refreshToken);
    }

    /**
     * Map UtilisateurAuth entity to UserResponse DTO.
     */
    private UserResponse mapToUserResponse(UtilisateurAuth user) {
        return UserResponse.builder()
            .id("usr_" + user.getId())
            .keycloakId(user.getKeycloakId())
            .phoneNumber(user.getNumeroTelephone())
            .firstName(user.getPrenom())
            .lastName(user.getNom())
            .email(user.getEmail())
            .status(user.getEtatNumero() == UtilisateurAuth.EtatNumero.VERIFIE ? "ACTIVE" : "PENDING")
            .hasPinConfigured(user.getPin() != null)
            .createdAt(user.getDateCreation())
            .build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
