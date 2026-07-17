package com.heng.cms.concessionservice.repository;

import com.heng.cms.concessionservice.domain.entity.ComboItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComboItemRepository extends JpaRepository<ComboItem, UUID> {
}
