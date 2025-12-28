package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.service.dto.OtpReponseDTO;

public interface OtpService {
    // Générer un OTP pour un numéro
    OtpReponseDTO genererOtp(String numeroTelephone);

    // Vérifier un OTP pour un numéro
    boolean verifierOtp(String numeroTelephone, String otp);
}
