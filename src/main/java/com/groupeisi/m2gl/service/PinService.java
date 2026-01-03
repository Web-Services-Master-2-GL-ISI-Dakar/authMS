package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.event.AuthEventProducer;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.request.ConfirmPinResetRequest;
import com.groupeisi.m2gl.service.dto.request.CreatePinRequest;
import com.groupeisi.m2gl.service.dto.request.UpdatePinRequest;
import com.groupeisi.m2gl.service.dto.response.CheckPhoneResponse;
import com.groupeisi.m2gl.service.dto.response.PinStatusResponse;
import com.groupeisi.m2gl.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for PIN management operations.
 */
@Service
@Transactional
public class PinService {

    private static final Logger LOG = LoggerFactory.getLogger(PinService.class);
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final UtilisateurAuthRepository userRepository;
    private final OtpService otpService;
    private final KeycloakService keycloakService;
    private final AuthEventProducer eventProducer;
    private final BCryptPasswordEncoder passwordEncoder;

    public PinService(
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
     * Get PIN status for user.
     */
    @Transactional(readOnly = true)
    public PinStatusResponse getPinStatus(String keycloakId) {
        LOG.debug("Getting PIN status for user: {}", keycloakId);

        UtilisateurAuth user = findUserByKeycloakId(keycloakId);

        return PinStatusResponse.builder()
            .hasPinConfigured(user.getPin() != null)
            .build();
    }

    /**
     * Create initial PIN for user (requires OTP).
     */
    public PinStatusResponse createPin(String keycloakId, CreatePinRequest request, String correlationId) {
        LOG.debug("Creating PIN for user: {}", keycloakId);

        UtilisateurAuth user = findUserByKeycloakId(keycloakId);

        // Check if PIN already exists
        if (user.getPin() != null) {
            throw new BadRequestAlertException("PIN already exists", "pin", "PIN_ALREADY_EXISTS");
        }

        // Verify OTP
        if (!otpService.verifierOtp(user.getNumeroTelephone(), request.getOtp())) {
            throw new BadRequestAlertException("Invalid or expired OTP", "pin", "INVALID_OTP");
        }

        // Set PIN
        user.setPin(passwordEncoder.encode(request.getPin()));
        userRepository.save(user);

        // Update PIN in Keycloak
        keycloakService.updateUserPin(keycloakId, request.getPin());

        LOG.info("PIN created successfully for user: {}", keycloakId);

        return PinStatusResponse.builder()
            .hasPinConfigured(true)
            .message("PIN created successfully")
            .build();
    }

    /**
     * Update existing PIN (requires current PIN).
     */
    public PinStatusResponse updatePin(String keycloakId, UpdatePinRequest request) {
        LOG.debug("Updating PIN for user: {}", keycloakId);

        UtilisateurAuth user = findUserByKeycloakId(keycloakId);

        // Verify current PIN
        if (user.getPin() == null || !passwordEncoder.matches(request.getCurrentPin(), user.getPin())) {
            throw new BadRequestAlertException("Invalid current PIN", "pin", "INVALID_CURRENT_PIN");
        }

        // Check if new PIN is different
        if (request.getCurrentPin().equals(request.getNewPin())) {
            throw new BadRequestAlertException("New PIN must be different", "pin", "PIN_SAME_AS_CURRENT");
        }

        // Update PIN
        user.setPin(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);

        // Update PIN in Keycloak
        keycloakService.updateUserPin(keycloakId, request.getNewPin());

        LOG.info("PIN updated successfully for user: {}", keycloakId);

        return PinStatusResponse.builder()
            .hasPinConfigured(true)
            .pinUpdatedAt(Instant.now())
            .message("PIN updated successfully")
            .build();
    }

    /**
     * Initiate PIN reset by sending OTP.
     */
    public CheckPhoneResponse initiateReset(String phoneNumber, String correlationId) {
        LOG.debug("Initiating PIN reset for: {}", maskPhone(phoneNumber));

        // Find user
        UtilisateurAuth user = userRepository.findByNumeroTelephone(phoneNumber)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "pin", "USER_NOT_FOUND"));

        // Generate OTP and publish event
        String otp = otpService.genererOtp(phoneNumber).getOtp();
        eventProducer.publishOtpForPinReset(phoneNumber, otp, correlationId);

        LOG.info("PIN reset OTP sent for: {}", maskPhone(phoneNumber));

        return CheckPhoneResponse.builder()
            .phoneNumber(phoneNumber)
            .otpSent(true)
            .otpExpiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
            .build();
    }

    /**
     * Confirm PIN reset with OTP and set new PIN.
     */
    public PinStatusResponse confirmReset(ConfirmPinResetRequest request, String correlationId) {
        LOG.debug("Confirming PIN reset for: {}", maskPhone(request.getPhoneNumber()));

        // Find user
        UtilisateurAuth user = userRepository.findByNumeroTelephone(request.getPhoneNumber())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "pin", "USER_NOT_FOUND"));

        // Verify OTP
        if (!otpService.verifierOtp(request.getPhoneNumber(), request.getOtp())) {
            throw new BadRequestAlertException("Invalid or expired OTP", "pin", "INVALID_OTP");
        }

        // Update PIN
        user.setPin(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);

        // Update PIN in Keycloak
        if (user.getKeycloakId() != null) {
            keycloakService.updateUserPin(user.getKeycloakId(), request.getNewPin());
        }

        LOG.info("PIN reset successful for: {}", maskPhone(request.getPhoneNumber()));

        return PinStatusResponse.builder()
            .hasPinConfigured(true)
            .pinUpdatedAt(Instant.now())
            .message("PIN reset successfully")
            .build();
    }

    /**
     * Find user by Keycloak ID.
     */
    private UtilisateurAuth findUserByKeycloakId(String keycloakId) {
        return userRepository.findAll().stream()
            .filter(u -> keycloakId.equals(u.getKeycloakId()))
            .findFirst()
            .orElseThrow(() -> new BadRequestAlertException("User not found", "user", "USER_NOT_FOUND"));
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
