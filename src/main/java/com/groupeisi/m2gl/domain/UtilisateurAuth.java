package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "utilisateur_auth")
public class UtilisateurAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_telephone", unique = true, nullable = false)
    private String numeroTelephone;

    @Column(name = "etat_numero", nullable = false)
    @Enumerated(EnumType.STRING)
    private EtatNumero etatNumero = EtatNumero.NON_VERIFIE;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation = Instant.now();

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public EtatNumero getEtatNumero() {
        return etatNumero;
    }

    public void setEtatNumero(EtatNumero etatNumero) {
        this.etatNumero = etatNumero;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    // --- Enum interne pour l'état du numéro ---
    public enum EtatNumero {
        NON_VERIFIE, // numéro ajouté mais OTP non validé
        VERIFIE, // numéro validé via OTP
        BLOQUE, // numéro bloqué pour raisons de sécurité
    }
}
