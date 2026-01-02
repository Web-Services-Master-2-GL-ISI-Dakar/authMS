package com.groupeisi.m2gl.service.dto;

import java.io.Serializable;

public class CreateWalletFromAuth implements Serializable {
    private String userId;
    private String phone;

    public CreateWalletFromAuth() {}

    public CreateWalletFromAuth(String userId, String phone) {
        this.userId = userId;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
