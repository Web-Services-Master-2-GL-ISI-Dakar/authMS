package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.InscriptionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InscriptionService {

    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final OtpService otpService;

    public InscriptionService(UtilisateurAuthRepository utilisateurAuthRepository, OtpService otpService) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.otpService = otpService;
    }

    public String inscrire(InscriptionDTO dto) {
        boolean otpValide = otpService.verifierOtp(dto.getNumeroTelephone(), dto.getOtp());

        if (!otpValide) {
            return "OTP invalide ou expiré";
        }

        UtilisateurAuth utilisateur = utilisateurAuthRepository
            .findByNumeroTelephone(dto.getNumeroTelephone())
            .orElse(new UtilisateurAuth());

        utilisateur.setNumeroTelephone(dto.getNumeroTelephone());
        utilisateur.setPin(dto.getPin());

        utilisateurAuthRepository.save(utilisateur);

        return "OTP validé, vous pouvez maintenant définir votre PIN.";
    }
}
