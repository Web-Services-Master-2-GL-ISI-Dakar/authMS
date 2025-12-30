package com.groupeisi.m2gl.service.dto;

public class VerifyOtpDTO {

    private String numeroTelephone;
    private String otp;
    private String pin; // optionnel, si défini après OTP

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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
