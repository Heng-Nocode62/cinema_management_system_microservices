package com.heng.cms.movieservice.spec;

import com.heng.cms.movieservice.domain.Genre;
import com.heng.cms.movieservice.domain.Movie;
import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MovieSpecification {

	private Specification<Movie> hasGenreId(Long genreId){
		return (root, query, cb)->{
			if(genreId == null) return null;
            if (query == null) throw new AssertionError();
            query.distinct(true);

			Join<Movie, Genre> genres = root.join("genres", JoinType.INNER);
			return cb.equal(genres.get("id"), genreId);
		};
	}

	private Specification<Movie> hasLanguage(String language) {
		return (root, query, cb) ->{
			if(language == null || language.isEmpty()) return null;
			return cb.equal(root.get("language"), language);
		};
	}

	private Specification<Movie> hasTitleLike(String title) {
		return (root, query, cb) ->{
			if(title == null || title.isEmpty()) return null;
			return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
		};
	}

	private Specification<Movie> hasStatus(MovieStatus status){
		return (root, query, cb) -> {
			if (status == null) return null;
			return cb.equal(root.get("status"), status);
		};
	}

	private Specification<Movie> releaseAfter(LocalDate date){
		return (root, query, cb) ->{
			if(date == null) return null;
			return cb.greaterThanOrEqualTo(root.get("releaseDate"), date);
		};
	}

	private Specification<Movie> isNotArchived(){
		return (root, query, cb) ->
				cb.notEqual(root.get("status"), MovieStatus.ARCHIVED);
	}
// // this approach is deprecated
//	public Specification<Movie> filter(MovieFilter movie) {
//		return Specification
//				.where(hasGenreId(movie != null ? movie.getGenreId() : null))
//				.and(hasStatus(movie != null ? movie.getMovieStatus() : null))
//				.and(hasTitleLike(movie != null ? movie.getTitle() : null))
//				.and(hasLanguage(movie != null ? movie.getLanguage() : null));
//	}

	public Specification<Movie> filter(MovieFilter movie) {
		return Specification.allOf(
				hasGenreId(movie != null ? movie.getGenreId() : null),
				hasStatus(movie != null ? movie.getMovieStatus() : null),
				hasTitleLike(movie != null ? movie.getTitle() : null),
				hasLanguage(movie != null ? movie.getLanguage() : null)
		);
	}
}
