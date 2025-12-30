package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "utilisateur_auth",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "numero_telephone"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "keycloak_id"),
    }
)
public class UtilisateurAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // AUTHENTIFICATION
    // =========================

    @Column(name = "numero_telephone", nullable = false)
    private String numeroTelephone;

    /**
     * PIN hashé (BCrypt)
     */
    @Column(name = "pin")
    private String pin;

    /**
     * Identifiant Keycloak
     * Rempli lors de /api/otp/verifier
     */
    @Column(name = "keycloak_id")
    private String keycloakId;

    // =========================
    // PROFIL UTILISATEUR
    // =========================

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "email", length = 150)
    private String email;

    // =========================
    // MÉTADONNÉES
    // =========================

    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation;

    @Column(name = "etat_numero", nullable = false)
    @Enumerated(EnumType.STRING)
    private EtatNumero etatNumero = EtatNumero.NON_VERIFIE;

    public enum EtatNumero {
        NON_VERIFIE,
        VERIFIE,
    }

    // =========================
    // LIFECYCLE JPA
    // =========================

    @PrePersist
    protected void onCreate() {
        this.dateCreation = Instant.now();
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

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

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public EtatNumero getEtatNumero() {
        return etatNumero;
    }

    public void setEtatNumero(EtatNumero etatNumero) {
        this.etatNumero = etatNumero;
    }
}
