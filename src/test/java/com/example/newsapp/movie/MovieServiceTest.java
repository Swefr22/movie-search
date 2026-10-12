package com.example.newsapp.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

class MovieServiceTest {

	@Test
	void searchesOmdbAndReturnsInceptionResults() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://www.omdbapi.com");
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(method(HttpMethod.GET))
				.andExpect(queryParam("s", "inception"))
				.andExpect(queryParam("apikey", "test-key"))
				.andRespond(withSuccess("""
						{"Search":[{"Title":"Inception","Year":"2010","imdbID":"tt1375666",
						"Type":"movie","Poster":"https://example.com/inception.jpg"}],
						"totalResults":"1","Response":"True"}
						""", MediaType.APPLICATION_JSON));

		MovieSearchResponse result = new MovieService(builder, "https://www.omdbapi.com", "test-key")
				.search("inception");

		assertThat(result.search()).hasSize(1);
		assertThat(result.search().get(0).title()).isEqualTo("Inception");
		assertThat(result.search().get(0).year()).isEqualTo("2010");
		assertThat(result.search().get(0).imdbId()).isEqualTo("tt1375666");
		assertThat(result.totalResults()).isEqualTo("1");
		server.verify();
	}

	@Test
	void rejectsSearchWhenApiKeyIsMissing() {
		MovieService service = new MovieService(RestClient.builder(), "https://www.omdbapi.com", "");

		assertThatThrownBy(() -> service.search("inception"))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("OMDb API key is not configured");
	}
}
