// domain/entity/Combo.java
package com.heng.cms.concessionservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "combos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Combo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) private String name;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;
    @Column(name = "savings_amount", precision = 8, scale = 2)
    private BigDecimal savingsAmount;

    @OneToMany(mappedBy = "combo")
    @Builder.Default
    private List<ComboItem> comboItems = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}