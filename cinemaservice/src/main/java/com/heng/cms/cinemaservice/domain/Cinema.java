package com.heng.cms.cinemaservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "cinemas")
public class Cinema  {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(name = "name", nullable = false,unique = true)
	private String name;

	@Column(nullable = false,unique = true,name = "location")
	private String location;

	@Column(nullable = false,unique = false,name = "city")
	private String city;
	@Column(nullable = false, unique = true,name = "phone")
	private String phone;
	@Column(nullable = false, unique = true, name = "email")
	@Email
	private String email;

	@OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
	private List<Screen> screens;

	@Column(nullable = false, updatable = false, name = "created_at")
	private Instant createdAt;
	@Column(name = "last_updated_at")
	private Instant lastUpdatedAt;

}
