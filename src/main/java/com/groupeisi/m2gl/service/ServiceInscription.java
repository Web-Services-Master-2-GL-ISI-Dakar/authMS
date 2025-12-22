package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceInscription {

    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final ServiceOtp serviceOtp;
    private final ServiceNotification serviceNotification;
    private final ServiceKeycloak serviceKeycloak;

    public ServiceInscription(
        UtilisateurAuthRepository utilisateurAuthRepository,
        ServiceOtp serviceOtp,
        ServiceNotification serviceNotification,
        ServiceKeycloak serviceKeycloak
    ) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.serviceOtp = serviceOtp;
        this.serviceNotification = serviceNotification;
        this.serviceKeycloak = serviceKeycloak;
    }

    // Inscription initiale et envoi OTP
    public void inscrire(String numeroTelephone) {
        boolean existe = utilisateurAuthRepository.existsByNumeroTelephone(numeroTelephone);
        if (existe) {
            throw new RuntimeException("Ce numéro existe déjà. Utilisez votre PIN.");
        }

        // Créer utilisateur AuthMS
        UtilisateurAuth utilisateur = new UtilisateurAuth();
        utilisateur.setNumeroTelephone(numeroTelephone);
        utilisateurAuthRepository.save(utilisateur);

        // Créer utilisateur Keycloak et récupérer l'ID
        String keycloakId = serviceKeycloak.creerUtilisateurKeycloak(numeroTelephone);
        utilisateur.setKeycloakId(keycloakId);
        utilisateurAuthRepository.save(utilisateur);

        // Générer et envoyer OTP
        String otp = serviceOtp.creerEtSauvegarderOtp(numeroTelephone);
        serviceNotification.envoyerOtp(numeroTelephone, otp);
    }

    // Définir le PIN après validation OTP
    public void definirPin(String numeroTelephone, String pin) {
        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(numeroTelephone)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setPin(pin); // Vous pouvez ajouter hashage ici pour sécurité
        utilisateur.setActif(true); // Activation du compte
        utilisateurAuthRepository.save(utilisateur);
    }
}
