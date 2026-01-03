package com.groupeisi.m2gl.web.rest.v1;

import com.groupeisi.m2gl.service.UserProfileService;
import com.groupeisi.m2gl.service.dto.request.UpdateProfileRequest;
import com.groupeisi.m2gl.service.dto.response.ApiResponse;
import com.groupeisi.m2gl.service.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user profile endpoints.
 * Base path: /api/v1/users
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * GET /api/v1/users/me
     * Get the profile of the authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        String userId = jwt.getSubject();
        LOG.debug("Get profile request for user: {}", userId);

        UserResponse response = userProfileService.getCurrentUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, correlationId));
    }

    /**
     * PUT /api/v1/users/me
     * Update the profile of the authenticated user.
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        
        String correlationId = getOrCreateCorrelationId(httpRequest);
        String userId = jwt.getSubject();
        LOG.info("Update profile request for user: {}", userId);

        UserResponse response = userProfileService.updateProfile(userId, request);
        
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
}
