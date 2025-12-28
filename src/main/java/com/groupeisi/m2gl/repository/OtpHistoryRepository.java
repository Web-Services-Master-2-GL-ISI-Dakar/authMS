package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.OtpHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpHistoryRepository extends JpaRepository<OtpHistory, Long> {
    Optional<OtpHistory> findTopByPhoneOrderByIdDesc(String phone);
}
