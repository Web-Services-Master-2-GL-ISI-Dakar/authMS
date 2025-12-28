package com.groupeisi.m2gl.service.dto;

public class AuthResponseDTO {

    private String token;
    private String phone;
    private boolean isNewUser;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String phone, boolean isNewUser) {
        this.token = token;
        this.phone = phone;
        this.isNewUser = isNewUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}
