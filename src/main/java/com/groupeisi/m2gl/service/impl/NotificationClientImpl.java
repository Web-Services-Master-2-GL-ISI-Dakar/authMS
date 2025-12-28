package com.groupeisi.m2gl.service.impl;

import org.springframework.stereotype.Service;

@Service
public class NotificationClientImpl {

    public void sendOtp(String phone, String otp) {
        // TODO : intégrer service SMS / Push Notification
        System.out.println("Envoi OTP " + otp + " au numéro " + phone);
    }
}
