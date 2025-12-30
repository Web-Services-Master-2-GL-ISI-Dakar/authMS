package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;

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
     * Vérifie l'existence Keycloak
     */
    boolean userExistsByNumeroTelephone(String numeroTelephone);
}
