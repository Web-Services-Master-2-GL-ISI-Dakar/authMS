package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurAuthRepository extends JpaRepository<UtilisateurAuth, Long> {
    boolean existsByNumeroTelephone(String numeroTelephone);
    Optional<UtilisateurAuth> findByNumeroTelephone(String numeroTelephone);
}
