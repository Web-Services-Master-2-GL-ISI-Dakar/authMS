package com.groupeisi.m2gl.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompletionKycDTO {

    @NotBlank
    private String numeroTelephone;

    @NotBlank
    private String otp;

    @NotBlank
    private String pin;

    @NotBlank
    @Size(max = 100)
    private String prenom;

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    // Getters & setters
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
