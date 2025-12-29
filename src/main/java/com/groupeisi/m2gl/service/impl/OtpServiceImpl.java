package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;

    public OtpServiceImpl(UtilisateurAuthRepository utilisateurAuthRepository) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
    }

    @Override
    public OtpReponseDTO genererOtp(String numeroTelephone) {
        boolean nouveauUtilisateur = !utilisateurAuthRepository.existsByNumeroTelephone(numeroTelephone);

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        if (nouveauUtilisateur) {
            UtilisateurAuth utilisateur = new UtilisateurAuth();
            utilisateur.setNumeroTelephone(numeroTelephone);
            utilisateurAuthRepository.save(utilisateur);
        }

        OtpReponseDTO reponse = new OtpReponseDTO();
        reponse.setNumeroTelephone(numeroTelephone);
        reponse.setOtp(otp);
        reponse.setNouveauUtilisateur(nouveauUtilisateur);

        return reponse;
    }

    @Override
    public boolean verifierOtp(String numeroTelephone, String otp) {
        // OTP volontairement éphémère (non stocké)
        return utilisateurAuthRepository.findByNumeroTelephone(numeroTelephone).isPresent();
    }

    @Override
    public void definirPin(String numeroTelephone, String pin) {
        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(numeroTelephone)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        utilisateur.setPin(pin);
        utilisateurAuthRepository.save(utilisateur);
    }
}
