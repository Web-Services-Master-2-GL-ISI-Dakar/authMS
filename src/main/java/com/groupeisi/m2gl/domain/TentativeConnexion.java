package com.groupeisi.m2gl.domaine;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tentative_connexion")
public class TentativeConnexion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroTelephone;

    private Instant dateTentative;

    private boolean succes;

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

    public Instant getDateTentative() {
        return dateTentative;
    }

    public void setDateTentative(Instant dateTentative) {
        this.dateTentative = dateTentative;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }
}
