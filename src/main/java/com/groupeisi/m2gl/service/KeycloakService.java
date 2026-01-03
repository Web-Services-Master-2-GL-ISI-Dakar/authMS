package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.service.dto.response.TokensResponse;

public interface KeycloakService {
    /**
     * Création de l'utilisateur Keycloak
     * username = numero_telephone
     * password = PIN (clair uniquement ici)
     */
    String createUserWithPin(UtilisateurAuth utilisateur, String pinClair);

    /**
     * Complétion KYC (nom, prénom, email)
     */
    void completeKyc(UtilisateurAuth utilisateur);

    /**
     * Réinitialisation du PIN
     */
    void resetPin(String keycloakId, String newPinClair);

    /**
     * Mise à jour du PIN utilisateur
     */
    void updateUserPin(String keycloakId, String newPinClair);

    /**
     * Mise à jour du profil utilisateur
     */
    void updateUserProfile(String keycloakId, String firstName, String lastName, String email);

    /**
     * Vérifie l'existence Keycloak
     */
    boolean userExistsByNumeroTelephone(String numeroTelephone);

    /**
     * Get tokens for user after authentication
     */
    TokensResponse getTokensForUser(String username, String pin);

    /**
     * Refresh access token using refresh token
     */
    TokensResponse refreshToken(String refreshToken);

    /**
     * Logout user and revoke tokens
     */
    void logout(String accessToken, String refreshToken);
}
