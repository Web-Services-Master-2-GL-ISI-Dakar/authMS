package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPinConfirmDTO {

    @NotBlank
    private String numeroTelephone;

    @NotBlank
    private String otp;

    @NotBlank
    private String nouveauPin;

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNouveauPin() {
        return nouveauPin;
    }

    public void setNouveauPin(String nouveauPin) {
        this.nouveauPin = nouveauPin;
    }
}
