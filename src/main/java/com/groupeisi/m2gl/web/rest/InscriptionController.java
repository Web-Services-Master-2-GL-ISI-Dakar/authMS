package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.InscriptionDTO;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {

    private final OtpService otpService;

    public InscriptionController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Générer OTP
    @PostMapping("/generer-otp")
    public ResponseEntity<OtpReponseDTO> genererOtp(@RequestParam String numeroTelephone) {
        OtpReponseDTO reponse = otpService.genererOtp(numeroTelephone);
        return ResponseEntity.ok(reponse);
    }

    // Vérifier OTP
    @PostMapping("/verifier-otp")
    public ResponseEntity<String> verifierOtp(@RequestBody InscriptionDTO dto) {
        boolean valide = otpService.verifierOtp(dto.getNumeroTelephone(), dto.getOtp());
        if (valide) {
            return ResponseEntity.ok("OTP validé, vous pouvez maintenant définir votre PIN.");
        } else {
            return ResponseEntity.badRequest().body("OTP invalide ou expiré.");
        }
    }
}
