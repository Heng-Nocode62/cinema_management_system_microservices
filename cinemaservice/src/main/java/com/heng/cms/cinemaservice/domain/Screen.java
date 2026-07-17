package com.heng.cms.cinemaservice.domain;

import com.heng.cms.cinemaservice.domain.enumeric.ScreenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "screens")
public class Screen{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "total_seats",nullable = false)
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type",nullable = false)
    private ScreenType screenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @Column(nullable = false,name = "created_at")
    private Instant createdAt;
    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;


}
