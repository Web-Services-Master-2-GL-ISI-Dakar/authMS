package com.groupeisi.m2gl.service.dto;

public class LoginResponseDTO {

    private boolean succes;
    private String message;
    private String keycloakId;

    // =========================
    // GETTERS & SETTERS
    // =========================
    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }
}
