package com.heng.cms.movieservice.repository;


import com.heng.cms.movieservice.domain.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
	boolean existsByName(String name);
	Page<Genre> findByDeletedFalse(Pageable pageable);
	Optional<Genre> findByIdAndDeletedFalse(UUID id);
	Optional<Genre> findByNameAndDeletedTrue(String name);

	Optional<Genre> findByName(String name);
}
