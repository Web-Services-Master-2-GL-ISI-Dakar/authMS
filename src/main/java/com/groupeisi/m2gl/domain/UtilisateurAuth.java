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

    @Column(name = "keycloak_id")
    private String keycloakId;

    @Column(name = "actif")
    private Boolean actif = false;

    @Column(name = "pin")
    private String pin; // ajout du champ PIN

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

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getPin() {
        return pin;
    } // getter pour PIN

    public void setPin(String pin) {
        this.pin = pin;
    } // setter pour PIN
}
