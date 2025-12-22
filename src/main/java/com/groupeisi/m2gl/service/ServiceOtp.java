package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.Otp;
import com.groupeisi.m2gl.repository.OtpRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class ServiceOtp {

    private final OtpRepository otpRepository;
    private static final SecureRandom random = new SecureRandom();

    public ServiceOtp(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String creerEtSauvegarderOtp(String numeroTelephone) {
        String code = String.valueOf(100000 + random.nextInt(900000));
        Otp otp = new Otp();
        otp.setNumeroTelephone(numeroTelephone);
        otp.setCodeOtp(code);
        otp.setExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        otpRepository.save(otp);
        return code;
    }

    public boolean verifierOtp(String numeroTelephone, String code) {
        return otpRepository
            .findByNumeroTelephone(numeroTelephone)
            .map(o -> o.getCodeOtp().equals(code) && o.getExpiration().isAfter(Instant.now()))
            .orElse(false);
    }
}
