package com.streamflix.controller;

import com.streamflix.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renders the StreamFlix homepage: hero section plus the movie category rows.
 */
@Controller
public class HomeController {

    private final MovieService movieService;

    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", movieService.getFeaturedMovie());
        model.addAttribute("trending", movieService.getTrending());
        model.addAttribute("popular", movieService.getPopular());
        model.addAttribute("action", movieService.getByGenre("Action"));
        model.addAttribute("scifi", movieService.getByGenre("Sci-Fi"));
        model.addAttribute("drama", movieService.getByGenre("Drama"));
        return "index";
    }
}
