package com.groupeisi.m2gl.service.impl;

import com.groupeisi.m2gl.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String phone, String message) {
        System.out.println("SMS envoyé à " + phone + " : " + message);
        // Implémentation réelle via API SMS possible ici
    }
}
