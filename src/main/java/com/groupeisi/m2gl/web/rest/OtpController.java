package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import com.groupeisi.m2gl.service.dto.VerifyOtpDTO;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Générer OTP
    @PostMapping("/generer")
    public ResponseEntity<OtpReponseDTO> genererOtp(@RequestBody Map<String, String> request) {
        String numeroTelephone = request.get("numeroTelephone");
        OtpReponseDTO reponse = otpService.genererOtp(numeroTelephone);
        return ResponseEntity.ok(reponse);
    }

    // Vérifier OTP et définir PIN
    @PostMapping("/verifier")
    public ResponseEntity<String> verifierOtp(@RequestBody VerifyOtpDTO dto) {
        boolean otpValide = otpService.verifierOtp(dto.getNumeroTelephone(), dto.getOtp());

        if (!otpValide) {
            return ResponseEntity.badRequest().body("OTP invalide ou expiré");
        }

        if (dto.getPin() != null && !dto.getPin().isEmpty()) {
            otpService.definirPin(dto.getNumeroTelephone(), dto.getPin());
        }

        return ResponseEntity.ok("OTP validé, vous pouvez maintenant définir votre PIN.");
    }
}
