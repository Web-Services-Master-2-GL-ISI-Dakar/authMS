package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.service.dto.OtpReponseDTO;

public interface OtpService {
    OtpReponseDTO genererOtp(String numeroTelephone);

    boolean verifierOtp(String numeroTelephone, String otp);
    void definirPin(String numeroTelephone, String pin);
}
