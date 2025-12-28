//package com.groupeisi.m2gl.domain;
//
//import jakarta.persistence.*;
//@Entity
//@Table(name = "utilisateur_auth")
//
//public class UserAuth {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "numero_telephone", unique = true, nullable = false)
//    private String numeroTelephone;
//
//    @Column(name = "actif")
//    private Boolean actif = false;
//
//    // getters & setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getNumeroTelephone() {
//        return numeroTelephone;
//    }
//
//    public void setNumeroTelephone(String numeroTelephone) {
//        this.numeroTelephone = numeroTelephone;
//    }
//
//    public Boolean getActif() {
//        return actif;
//    }
//
//    public void setActif(Boolean actif) {
//        this.actif = actif;
//    }
//}
//
//
