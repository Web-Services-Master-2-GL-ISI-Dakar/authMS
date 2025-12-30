package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.security.PinValidationService;
import com.groupeisi.m2gl.service.KeycloakService;
import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import java.time.Instant;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpServiceImpl implements OtpService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final PinValidationService pinValidationService;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;

    public OtpServiceImpl(
        UtilisateurAuthRepository utilisateurAuthRepository,
        PinValidationService pinValidationService,
        PasswordEncoder passwordEncoder,
        KeycloakService keycloakService
    ) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.pinValidationService = pinValidationService;
        this.passwordEncoder = passwordEncoder;
        this.keycloakService = keycloakService;
    }

    /**
     * Génère un OTP éphémère pour un numéro de téléphone.
     * Si l'utilisateur est nouveau, il est créé dans la base.
     */
    @Override
    @Transactional
    public OtpReponseDTO genererOtp(String numeroTelephone) {
        boolean nouveauUtilisateur = !utilisateurAuthRepository.existsByNumeroTelephone(numeroTelephone);

        // Génération OTP 6 chiffres aléatoire
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(numeroTelephone)
            .orElseGet(() -> {
                UtilisateurAuth newUser = new UtilisateurAuth();
                newUser.setNumeroTelephone(numeroTelephone);
                newUser.setDateCreation(Instant.now());
                newUser.setEtatNumero(UtilisateurAuth.EtatNumero.NON_VERIFIE);
                return utilisateurAuthRepository.save(newUser);
            });

        OtpReponseDTO reponse = new OtpReponseDTO();
        reponse.setNumeroTelephone(numeroTelephone);
        reponse.setOtp(otp);
        reponse.setNouveauUtilisateur(nouveauUtilisateur);

        return reponse;
    }

    /**
     * Vérifie l'OTP pour un numéro donné.
     * Ici, on simule la vérification avec l'existence de l'utilisateur.
     */
    @Override
    public boolean verifierOtp(String numeroTelephone, String otp) {
        return utilisateurAuthRepository.findByNumeroTelephone(numeroTelephone).isPresent();
    }

    /**
     * Définit le PIN de l'utilisateur.
     * Le PIN est validé, hashé et l'utilisateur est créé dans Keycloak si nécessaire.
     */
    @Override
    @Transactional
    public void definirPin(String numeroTelephone, String pin) {
        // Validation du PIN
        pinValidationService.verifierPin(pin);

        // Récupération de l'utilisateur
        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(numeroTelephone)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Hash du PIN avant stockage
        utilisateur.setPin(passwordEncoder.encode(pin));
        utilisateurAuthRepository.save(utilisateur);

        // Création utilisateur Keycloak si non encore lié
        if (utilisateur.getKeycloakId() == null) {
            String keycloakId = keycloakService.createUserWithPin(utilisateur, pin);
            utilisateur.setKeycloakId(keycloakId);
            utilisateurAuthRepository.save(utilisateur);
        }
    }
}
