// repository/InventoryRepository.java
package com.heng.cms.concessionservice.repository;

import com.heng.cms.concessionservice.domain.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {
    Optional<Inventory> findByCinemaIdAndMenuItemId(UUID cinemaId, UUID menuItemId);

    List<Inventory> findByCinemaId(UUID cinemaId);

    @Query("SELECT i FROM Inventory i WHERE i.cinemaId = :cinemaId AND i.availableQuantity <= i.reorderThreshold")
    List<Inventory> findLowStock(UUID cinemaId);

    boolean existsByCinemaIdAndMenuItemId(UUID cinemaId, UUID menuItemId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT i
    FROM Inventory i
    WHERE i.id IN :ids
        """)
    List<Inventory> findAllByIdForUpdate(Collection<UUID> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventory i
        WHERE cinemaId = :cinemaId
        AND i.menuItemId in :menuItemIds
        """)
    List<Inventory> findAllByCinemaIdAndMenuItemIdForUpdate(@Param("cinemaId") UUID cinemaId, @Param("menuItemIds") Set<UUID> menuItemIds);
}