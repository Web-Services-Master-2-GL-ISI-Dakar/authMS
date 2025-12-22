package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.NotBlank;

public class VerificationOtpDTO {

    @NotBlank
    private String numeroTelephone;

    @NotBlank
    private String codeOtp;

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getCodeOtp() {
        return codeOtp;
    }

    public void setCodeOtp(String codeOtp) {
        this.codeOtp = codeOtp;
    }
}
