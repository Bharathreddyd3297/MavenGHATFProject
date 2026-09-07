package com.streamflix.controller;

import com.streamflix.model.Movie;
import com.streamflix.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Handles movie details, search, and the "My List" feature.
 */
@Controller
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movie/{id}")
    public String movieDetails(@PathVariable int id, Model model) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("movie", movie.get());
        return "movie";
    }

    @PostMapping("/movie/{id}/add-to-my-list")
    public String addToMyList(@PathVariable int id) {
        movieService.addToMyList(id);
        return "redirect:/movie/" + id;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("query", query);
        model.addAttribute("results", movieService.searchMovies(query));
        return "search";
    }

    @GetMapping("/my-list")
    public String myList(Model model) {
        model.addAttribute("movies", movieService.getMyList());
        return "my-list";
    }
}
