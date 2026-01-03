package com.groupeisi.m2gl.web.rest.v1;

import com.groupeisi.m2gl.event.payload.OtpRequestedEvent.OtpPurpose;
import com.groupeisi.m2gl.service.AuthService;
import com.groupeisi.m2gl.service.dto.request.CheckPhoneRequest;
import com.groupeisi.m2gl.service.dto.request.CompleteSignUpRequest;
import com.groupeisi.m2gl.service.dto.request.LoginRequest;
import com.groupeisi.m2gl.service.dto.request.RefreshTokenRequest;
import com.groupeisi.m2gl.service.dto.request.ResendOtpRequest;
import com.groupeisi.m2gl.service.dto.response.ApiResponse;
import com.groupeisi.m2gl.service.dto.response.AuthResponse;
import com.groupeisi.m2gl.service.dto.response.CheckPhoneResponse;
import com.groupeisi.m2gl.service.dto.response.ResendOtpResponse;
import com.groupeisi.m2gl.service.dto.response.TokensResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for authentication endpoints.
 * Base path: /api/v1/auth
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/sign-up/check-phone
     * Check if phone number is registered. For new users, initiates OTP.
     */
    @PostMapping("/sign-up/check-phone")
    public ResponseEntity<ApiResponse<CheckPhoneResponse>> checkPhone(
            @Valid @RequestBody CheckPhoneRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Check phone request for: {}", maskPhone(request.getPhoneNumber()));

        CheckPhoneResponse response = authService.checkPhone(request.getPhoneNumber(), correlationId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/sign-up/resend-otp
     * Resend OTP code to user's phone number.
     */
    @PostMapping("/sign-up/resend-otp")
    public ResponseEntity<ApiResponse<ResendOtpResponse>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Resend OTP request for: {}", maskPhone(request.getPhoneNumber()));

        // Parse purpose, default to REGISTRATION
        OtpPurpose purpose = OtpPurpose.REGISTRATION;
        if (request.getPurpose() != null) {
            try {
                purpose = OtpPurpose.valueOf(request.getPurpose().toUpperCase());
            } catch (IllegalArgumentException e) {
                LOG.warn("Invalid OTP purpose: {}, defaulting to REGISTRATION", request.getPurpose());
            }
        }

        ResendOtpResponse response = authService.resendOtp(request.getPhoneNumber(), purpose, correlationId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/sign-up/complete
     * Complete user registration with OTP verification, PIN and KYC info.
     */
    @PostMapping("/sign-up/complete")
    public ResponseEntity<ApiResponse<AuthResponse>> completeSignUp(
            @Valid @RequestBody CompleteSignUpRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Complete sign-up request for: {}", maskPhone(request.getPhoneNumber()));

        AuthResponse response = authService.completeSignUp(request, correlationId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/login
     * Authenticate user with phone number and PIN.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Login request for: {}", maskPhone(request.getPhoneNumber()));

        AuthResponse response = authService.login(request, correlationId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/refresh
     * Refresh access token using refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokensResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.debug("Refresh token request");

        TokensResponse response = authService.refreshToken(request.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/logout
     * Invalidate current session and revoke tokens.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Logout request");

        String accessToken = extractToken(authHeader);
        String refreshToken = request != null ? request.getRefreshToken() : null;
        
        authService.logout(accessToken, refreshToken);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets correlation ID from header or generates a new one.
     */
    private String getOrCreateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }

    /**
     * Extracts token from Authorization header.
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    /**
     * Masks phone number for logging.
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }
}
