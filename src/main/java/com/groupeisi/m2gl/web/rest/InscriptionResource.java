package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.ServiceInscription;
import com.groupeisi.m2gl.service.dto.InscriptionDTO;
import com.groupeisi.m2gl.service.dto.ReponseAuthDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InscriptionResource {

    private final ServiceInscription serviceInscription;

    public InscriptionResource(ServiceInscription serviceInscription) {
        this.serviceInscription = serviceInscription;
    }

    @PostMapping("/inscription")
    public ResponseEntity<ReponseAuthDTO> inscrire(@Valid @RequestBody InscriptionDTO dto) {
        serviceInscription.inscrire(dto.getNumeroTelephone());
        return ResponseEntity.ok(new ReponseAuthDTO("Code OTP envoyé avec succès", true, dto.getNumeroTelephone()));
    }
}
