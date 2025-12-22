package com.groupeisi.m2gl.service;

import org.springframework.stereotype.Service;

@Service
public class ServiceKeycloak {

    public String creerUtilisateurKeycloak(String numeroTelephone) {
        // Ici vous appelez Keycloak via REST API ou SDK pour créer l'utilisateur
        // Retournez l'ID unique Keycloak
        return java.util.UUID.randomUUID().toString(); // simulation
    }
}
