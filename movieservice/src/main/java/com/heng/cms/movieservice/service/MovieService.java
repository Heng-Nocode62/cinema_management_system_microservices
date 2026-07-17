package com.heng.cms.movieservice.service;

import com.heng.cms.movieservice.dto.MovieRequest;
import com.heng.cms.movieservice.dto.MovieResponse;
import com.heng.cms.movieservice.dto.MovieUpdateRequest;
import com.heng.cms.movieservice.dto.PageResponse;
import com.heng.cms.movieservice.spec.MovieFilter;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MovieService {

    PageResponse<MovieResponse> getAll(MovieFilter movie, int page, int size);

    MovieResponse getById(UUID id);

    MovieResponse create(@Valid MovieRequest request);

    MovieResponse updateById(UUID id, @Valid MovieUpdateRequest request);

    void archiveMovie(UUID id);

    String uploadPoster(MultipartFile file);
}
