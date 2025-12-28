package com.groupeisi.m2gl.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    @PostMapping("/api/send-otp")
    void sendOtp(@RequestParam("phone") String phone, @RequestParam("otp") String otp);
}
