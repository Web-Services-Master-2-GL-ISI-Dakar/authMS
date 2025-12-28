package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import java.time.Instant;
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
            utilisateur.setEtatNumero(UtilisateurAuth.EtatNumero.NON_VERIFIE);
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
        UtilisateurAuth utilisateur = utilisateurAuthRepository.findByNumeroTelephone(numeroTelephone).orElse(null);

        if (utilisateur == null) return false;

        // Ici on considère OTP valide (vous pouvez stocker OTP avec date d'expiration si besoin)
        utilisateur.setEtatNumero(UtilisateurAuth.EtatNumero.VERIFIE);
        utilisateurAuthRepository.save(utilisateur);

        return true;
    }
}
