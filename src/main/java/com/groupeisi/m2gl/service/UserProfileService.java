package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.request.UpdateProfileRequest;
import com.groupeisi.m2gl.service.dto.response.UserResponse;
import com.groupeisi.m2gl.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user profile operations.
 */
@Service
@Transactional
public class UserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileService.class);

    private final UtilisateurAuthRepository userRepository;
    private final KeycloakService keycloakService;

    public UserProfileService(UtilisateurAuthRepository userRepository, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    /**
     * Get current user profile by Keycloak ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String keycloakId) {
        LOG.debug("Getting user profile for: {}", keycloakId);

        UtilisateurAuth user = findUserByKeycloakId(keycloakId);
        return mapToUserResponse(user);
    }

    /**
     * Update user profile.
     */
    public UserResponse updateProfile(String keycloakId, UpdateProfileRequest request) {
        LOG.debug("Updating profile for user: {}", keycloakId);

        UtilisateurAuth user = findUserByKeycloakId(keycloakId);

        // Update fields if provided
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setPrenom(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setNom(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check if email is already used by another user
            userRepository.findAll().stream()
                .filter(u -> request.getEmail().equals(u.getEmail()) && !u.getKeycloakId().equals(keycloakId))
                .findFirst()
                .ifPresent(u -> {
                    throw new BadRequestAlertException("Email already in use", "user", "EMAIL_ALREADY_EXISTS");
                });
            user.setEmail(request.getEmail());
        }

        // Save user
        user = userRepository.save(user);

        // Update in Keycloak
        keycloakService.updateUserProfile(keycloakId, user.getPrenom(), user.getNom(), user.getEmail());

        LOG.info("Profile updated for user: {}", keycloakId);

        return mapToUserResponse(user);
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
}
