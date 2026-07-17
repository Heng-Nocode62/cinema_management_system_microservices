package com.heng.cms.movieservice.repository;

import com.heng.cms.movieservice.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> , JpaSpecificationExecutor<Movie>{

    Optional<Movie> findByTitle(String title);
}
