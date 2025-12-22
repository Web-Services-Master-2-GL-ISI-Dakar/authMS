package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreationPinDTO {

    @NotBlank
    private String numeroTelephone;

    @NotBlank
    @Size(min = 4, max = 6)
    private String pin;

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
