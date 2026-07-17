package com.heng.cms.concessionservice.repository;

import com.heng.cms.concessionservice.domain.entity.Combo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComboRepository extends JpaRepository<Combo, UUID> {

    List<Combo> findAllByActiveTrue();

    Optional<Combo> findByIdAndActiveTrue(UUID comboId);

    boolean existsByNameAndActiveTrue(@NotBlank String name);

    List<Combo> findAllByIdInAndActiveTrue(Collection<UUID> ids);
}
