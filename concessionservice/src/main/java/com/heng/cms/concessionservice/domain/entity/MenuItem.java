// domain/entity/MenuItem.java
package com.heng.cms.concessionservice.domain.entity;

import com.heng.cms.concessionservice.domain.enumeric.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Category category; // snack | drink | meal | combo

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    private Integer calories;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_available")
    @Builder.Default
    private boolean available = true;

    @OneToMany(mappedBy = "menuItem",fetch = FetchType.LAZY)
    private List<ComboItem> comboItems;


    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
}