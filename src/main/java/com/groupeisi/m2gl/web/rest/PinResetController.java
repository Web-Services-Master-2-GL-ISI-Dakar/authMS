package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.PinResetService;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import com.groupeisi.m2gl.service.dto.ResetPinConfirmDTO;
import com.groupeisi.m2gl.service.dto.ResetPinStartDTO;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pin/reset")
public class PinResetController {

    private final PinResetService pinResetService;

    public PinResetController(PinResetService pinResetService) {
        this.pinResetService = pinResetService;
    }

    // =========================
    // START RESET PIN
    // =========================
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@Valid @RequestBody ResetPinStartDTO dto) {
        OtpReponseDTO otp = pinResetService.startReset(dto.getNumeroTelephone());
        Map<String, Object> response = new HashMap<>();

        if (otp == null) {
            response.put("success", false);
            response.put("message", "Utilisateur inexistant. Veuillez vous inscrire.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "OTP envoyé pour réinitialisation du PIN");
        response.put("otp", otp.getOtp()); // DEV ONLY
        return ResponseEntity.ok(response);
    }

    // =========================
    // CONFIRM RESET PIN
    // =========================
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirm(@Valid @RequestBody ResetPinConfirmDTO dto) {
        boolean success = pinResetService.confirmReset(dto.getNumeroTelephone(), dto.getOtp(), dto.getNouveauPin());
        Map<String, Object> response = new HashMap<>();

        if (!success) {
            response.put("success", false);
            response.put("message", "OTP invalide ou utilisateur inexistant");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "PIN réinitialisé avec succès !");
        return ResponseEntity.ok(response);
    }
}
