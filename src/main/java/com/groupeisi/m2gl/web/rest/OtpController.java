package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import com.groupeisi.m2gl.service.dto.VerifyOtpDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Générer un OTP pour un numéro de téléphone.
     */
    @PostMapping("/generer")
    public ResponseEntity<OtpReponseDTO> genererOtp(@RequestParam String numeroTelephone) {
        OtpReponseDTO reponse = otpService.genererOtp(numeroTelephone);
        return ResponseEntity.ok(reponse);
    }

    /**
     * Vérifier l'OTP et définir le PIN si nécessaire.
     */
    @PostMapping("/verifier")
    public ResponseEntity<String> verifierOtp(@RequestBody VerifyOtpDTO dto) {
        boolean otpValide = otpService.verifierOtp(dto.getNumeroTelephone(), dto.getOtp());

        if (!otpValide) {
            return ResponseEntity.badRequest().body("OTP invalide ou expiré");
        }

        // Définir le PIN si fourni
        if (dto.getPin() != null && !dto.getPin().isEmpty()) {
            otpService.definirPin(dto.getNumeroTelephone(), dto.getPin());
        }

        return ResponseEntity.ok("OTP validé, vous pouvez maintenant définir votre PIN.");
    }
}
