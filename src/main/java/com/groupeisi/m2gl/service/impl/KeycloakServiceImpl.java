package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakServiceImpl.class);

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public String createUserWithPin(UtilisateurAuth utilisateur, String pinClair) {
        UsersResource usersResource = keycloak.realm(realm).users();

        // Vérifier si l'utilisateur existe déjà
        if (userExistsByNumeroTelephone(utilisateur.getNumeroTelephone())) {
            log.info("Utilisateur déjà existant dans Keycloak: {}", utilisateur.getNumeroTelephone());
            return usersResource.search(utilisateur.getNumeroTelephone()).get(0).getId();
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(utilisateur.getNumeroTelephone());
        user.setEnabled(true);

        // Mot de passe = PIN
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(pinClair);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        log.info("Création utilisateur Keycloak: {}", utilisateur.getNumeroTelephone());
        Response response = usersResource.create(user);

        log.info("Status création Keycloak: {}", response.getStatus());
        if (response.getStatus() == 201) {
            String keycloakId = CreatedResponseUtil.getCreatedId(response);
            log.info("Utilisateur créé avec succès dans Keycloak, ID={}", keycloakId);
            return keycloakId;
        } else if (response.getStatus() == 409) {
            // Conflit, l'utilisateur existe déjà
            log.warn("Utilisateur déjà existant (409) dans Keycloak: {}", utilisateur.getNumeroTelephone());
            List<UserRepresentation> existing = usersResource.search(utilisateur.getNumeroTelephone());
            return existing.isEmpty() ? null : existing.get(0).getId();
        } else {
            String err = "Erreur création utilisateur Keycloak, status=" + response.getStatus();
            log.error(err);
            throw new IllegalStateException(err);
        }
    }

    @Override
    public void completeKyc(UtilisateurAuth utilisateur) {
        if (utilisateur.getKeycloakId() == null) {
            throw new IllegalStateException("Utilisateur non lié à Keycloak");
        }

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(utilisateur.getPrenom());
        user.setLastName(utilisateur.getNom());
        user.setEmail(utilisateur.getEmail());

        keycloak.realm(realm).users().get(utilisateur.getKeycloakId()).update(user);
        log.info("KYC complété pour utilisateur Keycloak ID={}", utilisateur.getKeycloakId());
    }

    @Override
    public void resetPin(String keycloakId, String newPinClair) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPinClair);
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(keycloakId).resetPassword(credential);
        log.info("PIN réinitialisé pour utilisateur Keycloak ID={}", keycloakId);
    }

    @Override
    public boolean userExistsByNumeroTelephone(String numeroTelephone) {
        try {
            List<UserRepresentation> users = keycloak.realm(realm).users().search(numeroTelephone, true);
            return !users.isEmpty();
        } catch (jakarta.ws.rs.NotFoundException e) {
            // Le realm n'existe pas ou Keycloak n'est pas accessible - considérer comme nouvel utilisateur
            log.warn("Keycloak realm '{}' non trouvé ou inaccessible. Considérant l'utilisateur comme nouveau.", realm);
            return false;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'utilisateur dans Keycloak: {}", e.getMessage());
            // En cas d'erreur, on considère que l'utilisateur n'existe pas pour permettre l'inscription
            return false;
        }
    }
}
