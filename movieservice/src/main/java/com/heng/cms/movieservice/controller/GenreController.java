package com.heng.cms.movieservice.controller;

import com.heng.cms.movieservice.dto.GenreRequest;
import com.heng.cms.movieservice.dto.GenreResponse;
import com.heng.cms.movieservice.dto.PageResponse;
import com.heng.cms.movieservice.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/genres")
@RequiredArgsConstructor
public class GenreController {
	private final GenreService genreService;
	
	@PostMapping
	public ResponseEntity<GenreResponse> createGenre(
			@Valid @RequestBody GenreRequest request){
		
		return new ResponseEntity<>(genreService.createGenre(request), HttpStatus.CREATED);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<GenreResponse> getGenreById(@PathVariable("id") UUID id){
		return ResponseEntity.ok().body(genreService.getById(id));
	}
	
	
	
	
	@GetMapping
	public ResponseEntity<PageResponse<GenreResponse>> findAllGenre(
			@RequestParam(name = "page",defaultValue = "0", required = false) int page,
			@RequestParam(name ="size", defaultValue = "10", required = false) int size){
		return ResponseEntity.ok().body(genreService.getAll(page, size));
	}
	
	
	
	@PutMapping("{id}")
	public ResponseEntity<?> updateGenre(
			@PathVariable("id") UUID id,
			@Valid @RequestBody GenreRequest genreDto){
		genreService.updateGenre(id, genreDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteGenre(@PathVariable("id") UUID id){
		genreService.deleteGenre(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
}
