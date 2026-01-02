package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.client.NotificationClient;
import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.notif.wsdl.OtpResponse;
import com.groupeisi.m2gl.repository.UtilisateurAuthRepository;
import com.groupeisi.m2gl.service.dto.CompletionKycDTO;
import com.groupeisi.m2gl.service.dto.CreateWalletFromAuth;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class InscriptionService {

    private static final Logger log = LoggerFactory.getLogger(InscriptionService.class);
    private final UtilisateurAuthRepository utilisateurAuthRepository;
    private final OtpService otpService;
    private final KeycloakService keycloakService;
    private final NotificationClient notificationClient;
    private final RestTemplate restTemplate;

    public InscriptionService(UtilisateurAuthRepository utilisateurAuthRepository, OtpService otpService, KeycloakService keycloakService, NotificationClient notificationClient, RestTemplateBuilder restTemplateBuilder) {
        this.utilisateurAuthRepository = utilisateurAuthRepository;
        this.otpService = otpService;
        this.keycloakService = keycloakService;
        this.notificationClient = notificationClient;
        this.restTemplate = restTemplateBuilder.build();
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
            String otp = otpService.genererOtp(numero).getOtp();
            OtpResponse otpResponse = notificationClient.sendOtp(numero, otp, 5);
            if (otpResponse.getStatus().isSuccess()) {
                log.info("OTP envoyé avec succès au numéro : {}", numero);
            } else {
                log.error("Échec de l'envoi de l'OTP au numéro : {}. Statut : {}", numero, otpResponse.getStatus().getMessage());
            }
            reponse.setOtp(null);
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
        CreateWalletFromAuth createWalletFromAuth = new CreateWalletFromAuth();
        createWalletFromAuth.setUserId(utilisateur.getId().toString());
        createWalletFromAuth.setPhone(utilisateur.getNumeroTelephone());
        createWallet(createWalletFromAuth);
        return "Inscription complète réussie !";
    }

    private void createWallet(CreateWalletFromAuth createWalletFromAuth) {
        restTemplate.postForEntity(
            "https://localhost:8080/api/v0/wallets",
            createWalletFromAuth,
            Void.class
        );
    }

}
