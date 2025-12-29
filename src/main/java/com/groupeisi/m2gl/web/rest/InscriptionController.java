package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.InscriptionService;
import com.groupeisi.m2gl.service.dto.InscriptionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping("/valider")
    public ResponseEntity<String> valider(@RequestBody InscriptionDTO dto) {
        return ResponseEntity.ok(inscriptionService.inscrire(dto));
    }
}
