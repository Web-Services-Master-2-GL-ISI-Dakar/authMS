package com.groupeisi.m2gl.service.dto;

import java.io.Serializable;

public class OtpReponseDTO implements Serializable {

    private String numeroTelephone;
    private String otp;
    private boolean nouveauUtilisateur;

    // --- Getters & Setters ---
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

    public boolean isNouveauUtilisateur() {
        return nouveauUtilisateur;
    }

    public void setNouveauUtilisateur(boolean nouveauUtilisateur) {
        this.nouveauUtilisateur = nouveauUtilisateur;
    }
}
