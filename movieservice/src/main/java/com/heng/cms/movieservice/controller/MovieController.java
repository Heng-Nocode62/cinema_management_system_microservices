package com.heng.cms.movieservice.controller;


import com.heng.cms.movieservice.dto.MovieRequest;
import com.heng.cms.movieservice.dto.MovieResponse;
import com.heng.cms.movieservice.dto.MovieUpdateRequest;
import com.heng.cms.movieservice.dto.PageResponse;
import com.heng.cms.movieservice.service.MovieService;
import com.heng.cms.movieservice.spec.MovieFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
	private final MovieService movieService;

	@GetMapping
	public ResponseEntity<PageResponse<MovieResponse>> getAllMovie(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name ="size", defaultValue = "10", required = false) int size,
			@ModelAttribute MovieFilter movie){
		
		return ResponseEntity.ok().body(movieService.getAll( movie,page,size));
	}

	@GetMapping("{id}")
	public ResponseEntity<MovieResponse> getMovieById(@PathVariable("id") UUID id){
		return ResponseEntity.ok(movieService.getById(id));
	}

	@PostMapping
	public ResponseEntity<MovieResponse> createMovie(
			 @Valid @RequestBody MovieRequest request){
		return ResponseEntity.ok(movieService.create(request));
		
	}

	@PutMapping("{id}")
	public ResponseEntity<MovieResponse> updateMovieById(
			@PathVariable("id") UUID id,
			@Valid @RequestBody MovieUpdateRequest request
			){
		return ResponseEntity.ok().body(movieService.updateById(id,request));
	}
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMovieById(@PathVariable("id") UUID id){
		movieService.archiveMovie(id);
	}



	@PostMapping("poster/upload")
	public ResponseEntity<String> uploadPoster(@RequestPart("file") MultipartFile file){
		return ResponseEntity.ok().body(movieService.uploadPoster(file));
	}
	
	

}
