package com.groupeisi.m2gl.repository;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.sn;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurAuthRepository extends JpaRepository<UtilisateurAuth, Long> {
    Optional<UtilisateurAuth> findByNumeroTelephone(String numeroTelephone);
    boolean existsByNumeroTelephone(String numeroTelephone);
}
