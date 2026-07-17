// domain/entity/Inventory.java
package com.heng.cms.concessionservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory",
    uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "cinema_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "menu_item_id",   nullable = false)
    private UUID menuItemId;

    @Column(name = "cinema_id", nullable = false)
    private UUID cinemaId;

    @Column(nullable = false, name = "available_quantity")
    @Builder.Default private Integer availableQuantity = 0;

    @Column(nullable = false, name = "reserve_quantity")
    @Builder.Default
    private Integer reserveQuantity = 0;
    @Column(name = "reorder_threshold")
    @Builder.Default
    private Integer reorderThreshold = 10;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}