package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.Otp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByNumeroTelephone(String numeroTelephone);
}
