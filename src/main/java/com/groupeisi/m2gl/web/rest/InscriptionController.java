package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.InscriptionService;
import com.groupeisi.m2gl.service.dto.CheckNumberDTO;
import com.groupeisi.m2gl.service.dto.CompletionKycDTO;
import com.groupeisi.m2gl.service.dto.OtpReponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    // Vérifier si le numéro existe et générer OTP si nouveau
    @PostMapping("/check-number")
    public ResponseEntity<OtpReponseDTO> checkNumero(@Valid @RequestBody CheckNumberDTO dto) {
        OtpReponseDTO reponse = inscriptionService.verifierOuGenererOtp(dto.getNumeroTelephone());
        return ResponseEntity.ok(reponse);
    }

    // Compléter inscription avec OTP, PIN et KYC
    @PostMapping("/completion")
    public ResponseEntity<String> completionKyc(@Valid @RequestBody CompletionKycDTO dto) {
        String result = inscriptionService.inscrireComplet(dto);
        return ResponseEntity.ok(result);
    }
}
