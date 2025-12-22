package com.groupeisi.m2gl.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceNotification {

    private final RestTemplate restTemplate = new RestTemplate();

    public void envoyerOtp(String numeroTelephone, String codeOtp) {
        String url = "http://notificationms/api/notifications/sms";
        String message = "Votre code OndMoney est : " + codeOtp;
        NotificationRequest request = new NotificationRequest(numeroTelephone, message);
        restTemplate.postForObject(url, request, Void.class);
    }

    private static class NotificationRequest {

        public String numero;
        public String message;

        public NotificationRequest(String numero, String message) {
            this.numero = numero;
            this.message = message;
        }
    }
}
