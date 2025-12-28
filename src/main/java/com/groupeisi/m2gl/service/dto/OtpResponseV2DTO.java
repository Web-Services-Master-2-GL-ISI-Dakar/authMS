package com.groupeisi.m2gl.service.dto;

import java.io.Serializable;

public class OtpResponseV2DTO implements Serializable {

    private String phone;
    private String otp;
    private boolean newUser;

    public OtpResponseV2DTO() {}

    public OtpResponseV2DTO(String phone, String otp, boolean newUser) {
        this.phone = phone;
        this.otp = otp;
        this.newUser = newUser;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}
