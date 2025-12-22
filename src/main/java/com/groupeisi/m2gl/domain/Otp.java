package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_telephone", nullable = false)
    private String numeroTelephone;

    @Column(name = "code_otp", nullable = false)
    private String codeOtp;

    @Column(name = "expiration", nullable = false)
    private Instant expiration;

    // getters & setters
    public Long getId() {
        return id;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getCodeOtp() {
        return codeOtp;
    }

    public void setCodeOtp(String codeOtp) {
        this.codeOtp = codeOtp;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }
}
