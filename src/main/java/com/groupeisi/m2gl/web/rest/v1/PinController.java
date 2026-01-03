package com.groupeisi.m2gl.web.rest.v1;

import com.groupeisi.m2gl.service.PinService;
import com.groupeisi.m2gl.service.dto.request.ConfirmPinResetRequest;
import com.groupeisi.m2gl.service.dto.request.CreatePinRequest;
import com.groupeisi.m2gl.service.dto.request.ResetPinRequest;
import com.groupeisi.m2gl.service.dto.request.UpdatePinRequest;
import com.groupeisi.m2gl.service.dto.response.ApiResponse;
import com.groupeisi.m2gl.service.dto.response.CheckPhoneResponse;
import com.groupeisi.m2gl.service.dto.response.PinStatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for PIN management endpoints.
 * Base path: /api/v1/auth/pin
 */
@RestController
@RequestMapping("/api/v1/auth/pin")
public class PinController {

    private static final Logger LOG = LoggerFactory.getLogger(PinController.class);

    private final PinService pinService;

    public PinController(PinService pinService) {
        this.pinService = pinService;
    }

    /**
     * GET /api/v1/auth/pin
     * Check if the authenticated user has a PIN configured.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PinStatusResponse>> getPinStatus(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        String userId = jwt.getSubject();
        LOG.debug("Get PIN status for user: {}", userId);

        PinStatusResponse response = pinService.getPinStatus(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/pin
     * Create initial PIN (requires OTP verification).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PinStatusResponse>> createPin(
            @Valid @RequestBody CreatePinRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        String userId = jwt.getSubject();
        LOG.info("Create PIN request for user: {}", userId);

        PinStatusResponse response = pinService.createPin(userId, request, correlationId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, correlationId));
    }

    /**
     * PUT /api/v1/auth/pin
     * Update existing PIN (requires current PIN).
     */
    @PutMapping
    public ResponseEntity<ApiResponse<PinStatusResponse>> updatePin(
            @Valid @RequestBody UpdatePinRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        String userId = jwt.getSubject();
        LOG.info("Update PIN request for user: {}", userId);

        PinStatusResponse response = pinService.updatePin(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/pin/reset
     * Initiate PIN reset (sends OTP to phone).
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<CheckPhoneResponse>> initiateReset(
            @Valid @RequestBody ResetPinRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Initiate PIN reset for: {}", maskPhone(request.getPhoneNumber()));

        CheckPhoneResponse response = pinService.initiateReset(request.getPhoneNumber(), correlationId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * POST /api/v1/auth/pin/reset/confirm
     * Confirm PIN reset with OTP and set new PIN.
     */
    @PostMapping("/reset/confirm")
    public ResponseEntity<ApiResponse<PinStatusResponse>> confirmReset(
            @Valid @RequestBody ConfirmPinResetRequest request,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        LOG.info("Confirm PIN reset for: {}", maskPhone(request.getPhoneNumber()));

        PinStatusResponse response = pinService.confirmReset(request, correlationId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
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
     * Masks phone number for logging.
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }
}
