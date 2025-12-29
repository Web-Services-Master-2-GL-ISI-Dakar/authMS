package com.groupeisi.m2gl.service.dto;

import java.io.Serializable;

public class VerifyOtpDTO implements Serializable {

    private String numeroTelephone;
    private String otp;
    private String pin; // PIN à définir après vérification OTP

    public VerifyOtpDTO() {}

    public VerifyOtpDTO(String numeroTelephone, String otp, String pin) {
        this.numeroTelephone = numeroTelephone;
        this.otp = otp;
        this.pin = pin;
    }

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
