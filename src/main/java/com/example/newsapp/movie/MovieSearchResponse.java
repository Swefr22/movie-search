package com.example.newsapp.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MovieSearchResponse(
		@JsonProperty("Search") List<Movie> search,
		String totalResults,
		String response,
		String error) {
}
