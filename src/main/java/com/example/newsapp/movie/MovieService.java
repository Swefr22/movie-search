package com.example.newsapp.movie;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovieService {

	private final RestClient restClient;
	private final String apiKey;

	public MovieService(
			RestClient.Builder restClientBuilder,
			@Value("${omdb.api.url:https://www.omdbapi.com}") String apiUrl,
			@Value("${omdb.api-key:}") String apiKey) {
		this.restClient = restClientBuilder.baseUrl(apiUrl).build();
		this.apiKey = apiKey;
	}

	public MovieSearchResponse search(String title) {
		if (title == null || title.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie title must not be blank");
		}
		if (apiKey.isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.SERVICE_UNAVAILABLE, "OMDb API key is not configured; set OMDB_API_KEY");
		}

		try {
			MovieSearchResponse response = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.queryParam("s", title.trim())
							.queryParam("apikey", apiKey)
							.build())
					.retrieve()
					.body(MovieSearchResponse.class);

			if (response == null) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OMDb returned an empty response");
			}
			if ("False".equalsIgnoreCase(response.response())
					&& response.error() != null
					&& !"Movie not found!".equalsIgnoreCase(response.error())) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OMDb search failed: " + response.error());
			}
			if (response.search() == null) {
				return new MovieSearchResponse(List.of(), "0", response.response(), response.error());
			}
			return response;
		} catch (RestClientException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OMDb request failed", exception);
		}
	}
}
