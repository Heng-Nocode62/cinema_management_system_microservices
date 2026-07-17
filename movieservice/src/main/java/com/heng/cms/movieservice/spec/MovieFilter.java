package com.heng.cms.movieservice.spec;

import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieFilter {
	private Long genreId;
	private String title;
	private String genreName;
	private String language;
	private MovieStatus movieStatus;

}
