package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.ServiceInscription;
import com.groupeisi.m2gl.service.ServiceOtp;
import com.groupeisi.m2gl.service.dto.CreationPinDTO;
import com.groupeisi.m2gl.service.dto.ReponseAuthDTO;
import com.groupeisi.m2gl.service.dto.VerificationOtpDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpResource {

    private final ServiceOtp serviceOtp;
    private final ServiceInscription serviceInscription;

    public OtpResource(ServiceOtp serviceOtp, ServiceInscription serviceInscription) {
        this.serviceOtp = serviceOtp;
        this.serviceInscription = serviceInscription;
    }

    @PostMapping("/verifier")
    public ResponseEntity<ReponseAuthDTO> verifierOtp(@Valid @RequestBody VerificationOtpDTO dto) {
        boolean valide = serviceOtp.verifierOtp(dto.getNumeroTelephone(), dto.getCodeOtp());
        if (!valide) {
            return ResponseEntity.ok(new ReponseAuthDTO("OTP invalide ou expiré", false, dto.getNumeroTelephone()));
        }
        return ResponseEntity.ok(new ReponseAuthDTO("OTP valide", true, dto.getNumeroTelephone()));
    }

    @PostMapping("/creation-pin")
    public ResponseEntity<ReponseAuthDTO> creationPin(@Valid @RequestBody CreationPinDTO dto) {
        serviceInscription.definirPin(dto.getNumeroTelephone(), dto.getPin());
        return ResponseEntity.ok(new ReponseAuthDTO("PIN créé avec succès", true, dto.getNumeroTelephone()));
    }
}
