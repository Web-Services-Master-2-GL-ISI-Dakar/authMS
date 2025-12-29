package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur_auth")
public class UtilisateurAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_telephone", unique = true, nullable = false)
    private String numeroTelephone;

    @Column(name = "pin")
    private String pin;

    public Long getId() {
        return id;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
