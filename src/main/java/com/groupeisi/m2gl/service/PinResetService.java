package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PinResetService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final OtpService otpService;

    public PinResetService(UtilisateurAuthRepository utilisateurAuthRepository, OtpService otpService) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.otpService = otpService;
    }

    // =========================
    // START RESET PIN
    // =========================
    public OtpReponseDTO startReset(String numeroTelephone) {
        UtilisateurAuth utilisateur = utilisateurAuthRepository.findByNumeroTelephone(numeroTelephone).orElse(null);

        if (utilisateur == null) {
            return null;
        }

        return otpService.genererOtp(numeroTelephone);
    }

    // =========================
    // CONFIRM RESET PIN
    // =========================
    public boolean confirmReset(String numeroTelephone, String otp, String nouveauPin) {
        if (!otpService.verifierOtp(numeroTelephone, otp)) {
            return false;
        }

        UtilisateurAuth utilisateur = utilisateurAuthRepository.findByNumeroTelephone(numeroTelephone).orElse(null);

        if (utilisateur == null) {
            return false;
        }

        utilisateur.setPin(nouveauPin);
        utilisateurAuthRepository.save(utilisateur);

        return true;
    }
}
