package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.CompletionKycDTO;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InscriptionService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final OtpService otpService;
    private final KeycloakService keycloakService;

    public InscriptionService(UtilisateurAuthRepository utilisateurAuthRepository, OtpService otpService, KeycloakService keycloakService) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.otpService = otpService;
        this.keycloakService = keycloakService;
    }

    // Vérifie et génère OTP si nouveau utilisateur
    public OtpReponseDTO verifierOuGenererOtp(String numero) {
        boolean exists =
            utilisateurAuthRepository.findByNumeroTelephone(numero).isPresent() || keycloakService.userExistsByNumeroTelephone(numero);

        OtpReponseDTO reponse = new OtpReponseDTO();
        reponse.setNumeroTelephone(numero);

        if (exists) {
            reponse.setNouveauUtilisateur(false);
            reponse.setOtp(null);
        } else {
            reponse.setNouveauUtilisateur(true);
            reponse.setOtp(otpService.genererOtp(numero).getOtp());
        }
        return reponse;
    }

    // Inscription complète
    public String inscrireComplet(CompletionKycDTO dto) {
        if (!otpService.verifierOtp(dto.getNumeroTelephone(), dto.getOtp())) {
            return "OTP invalide ou expiré";
        }

        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(dto.getNumeroTelephone())
            .orElse(new UtilisateurAuth());

        utilisateur.setNumeroTelephone(dto.getNumeroTelephone());
        utilisateur.setPin(dto.getPin());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setNom(dto.getNom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setEtatNumero(UtilisateurAuth.EtatNumero.VERIFIE);

        if (utilisateur.getKeycloakId() == null) {
            String keycloakId = keycloakService.createUserWithPin(utilisateur, dto.getPin());
            utilisateur.setKeycloakId(keycloakId);
            keycloakService.completeKyc(utilisateur);
        }

        utilisateurAuthRepository.save(utilisateur);
        return "Inscription complète réussie !";
    }
}
