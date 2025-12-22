package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.NotBlank;

public class InscriptionDTO {

    @NotBlank
    private String numeroTelephone;

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }
}
