package com.heng.cms.movieservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "genres")
public class Genre{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

	@Column(nullable = false, unique = true,name = "name")
	private String name;

	@ManyToMany(mappedBy = "genres")
	private Set<Movie> movies;
	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "created_at",nullable = false)
	private Instant createdAt;
	@Column(name = "last_updated_at")
	private Instant lastUpdatedAt;

}
