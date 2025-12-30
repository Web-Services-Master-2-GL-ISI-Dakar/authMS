package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CheckNumberDTO {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 15, message = "Le numéro de téléphone ne doit pas dépasser 15 caractères")
    private String numeroTelephone;

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }
}
