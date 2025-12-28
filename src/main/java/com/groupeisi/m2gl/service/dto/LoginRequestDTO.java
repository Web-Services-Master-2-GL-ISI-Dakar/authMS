package com.groupeisi.m2gl.service.dto;

public class LoginRequestDTO {

    private String phone;
    private String pin;

    // Constructeurs
    public LoginRequestDTO() {}

    public LoginRequestDTO(String phone, String pin) {
        this.phone = phone;
        this.pin = pin;
    }

    // Getters / Setters
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
