package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.UtilisateurAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurAuthRepository extends JpaRepository<UtilisateurAuth, Long> {
    // Vérifie si un numéro existe
    boolean existsByNumeroTelephone(String numeroTelephone);

    // Récupère l'utilisateur par son numéro
    Optional<UtilisateurAuth> findByNumeroTelephone(String numeroTelephone);
}
