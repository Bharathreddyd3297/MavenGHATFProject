package com.streamflix.service;

import com.streamflix.model.Movie;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieServiceTest {

    private final MovieService movieService = new MovieService();

    @Test
    void returnsAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        assertThat(movies).isNotEmpty();
        assertThat(movies).hasSize(12);
    }

    @Test
    void searchByTitleFindsMatchingMovie() {
        List<Movie> results = movieService.searchMovies("Quantum");

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(movie -> movie.getTitle().contains("Quantum"));
    }

    @Test
    void searchByGenreFindsMatchingMovies() {
        List<Movie> results = movieService.searchMovies("Action");

        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(movie -> movie.getGenre().equalsIgnoreCase("Action"));
    }

    @Test
    void searchWithNoMatchesReturnsEmptyList() {
        List<Movie> results = movieService.searchMovies("NoSuchMovieTitle123");

        assertThat(results).isEmpty();
    }
}
