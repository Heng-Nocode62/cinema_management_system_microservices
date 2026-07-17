// repository/MenuItemRepository.java
package com.heng.cms.concessionservice.repository;

import com.heng.cms.concessionservice.domain.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByAvailableTrue();
    List<MenuItem> findByCategoryAndAvailableTrue(String category);
    boolean existsByNameAndAvailableTrue(String name);

    List<MenuItem> findAllByIdInAndAvailableTrue(Collection<UUID> ids);
}