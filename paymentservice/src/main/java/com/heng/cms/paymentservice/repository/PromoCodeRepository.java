package com.heng.cms.paymentservice.repository;

import com.heng.cms.paymentservice.domain.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {

    Optional<PromoCode> findByCodeIgnoreCase(String code);

    @Modifying
    @Query("""
        UPDATE PromoCode p SET p.usesCount = p.usesCount+1 WHERE p.code = :code
        """)
    void incrementUsesCount(String code);

}
