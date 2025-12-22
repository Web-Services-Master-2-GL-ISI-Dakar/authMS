package com.groupeisi.m2gl.service.dto;

public class ReponseAuthDTO {

    private String message;
    private boolean success;
    private String numeroTelephone;

    public ReponseAuthDTO() {}

    public ReponseAuthDTO(String message, boolean success, String numeroTelephone) {
        this.message = message;
        this.success = success;
        this.numeroTelephone = numeroTelephone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }
}
