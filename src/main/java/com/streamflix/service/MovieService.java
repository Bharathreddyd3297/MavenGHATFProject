package com.streamflix.service;

import com.streamflix.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds the in-memory movie catalog and "My List" for the whole application.
 *
 * There is NO database here on purpose (see Phase 1 requirements). Everything
 * lives in memory for as long as the application keeps running. Restarting
 * the app resets "My List" back to empty.
 */
@Service
public class MovieService {

    private final List<Movie> movies = new ArrayList<>();

    // Movies the user has added to "My List" while the app is running.
    // CopyOnWriteArrayList is used so this stays safe if multiple browser
    // tabs/requests hit the app at the same time.
    private final List<Movie> myList = new CopyOnWriteArrayList<>();

    public MovieService() {
        movies.add(new Movie(1, "The Last Horizon",
                "A crew on a one-way mission must decide whether to save their ship or a colony at the edge of known space.",
                "Sci-Fi", 2024, 8.7, "128 min", "poster-scifi", true));

        movies.add(new Movie(2, "Code Runner",
                "A software engineer discovers a hidden exploit that puts her entire company's data at risk overnight.",
                "Action", 2023, 8.1, "110 min", "poster-action", false));

        movies.add(new Movie(3, "Dark Valley",
                "Two estranged brothers return to their hometown to confront a secret their family buried decades ago.",
                "Drama", 2022, 7.8, "122 min", "poster-drama", false));

        movies.add(new Movie(4, "Ocean Zero",
                "A deep-sea research team loses contact with the surface just as they uncover something that shouldn't exist.",
                "Sci-Fi", 2023, 8.3, "115 min", "poster-scifi", false));

        movies.add(new Movie(5, "The Silent City",
                "A journalist investigates why an entire town has stopped speaking to outsiders.",
                "Drama", 2021, 7.5, "105 min", "poster-drama", false));

        movies.add(new Movie(6, "Quantum Mission",
                "An experimental jump drive strands a crew between two timelines with only one chance to get home.",
                "Sci-Fi", 2024, 8.9, "132 min", "poster-scifi", false));

        movies.add(new Movie(7, "Red Planet",
                "The first supply convoy to a Mars outpost is ambushed, and the survivors must fight to complete the mission.",
                "Action", 2022, 7.9, "118 min", "poster-action", false));

        movies.add(new Movie(8, "Night Protocol",
                "An off-duty analyst uncovers a covert operation running inside her own agency.",
                "Action", 2023, 8.0, "108 min", "poster-action", false));

        movies.add(new Movie(9, "Hidden Truth",
                "A small-town lawyer takes on a case that forces her to confront her own family's past.",
                "Drama", 2021, 7.6, "112 min", "poster-drama", false));

        movies.add(new Movie(10, "Final Signal",
                "A lone radio operator on an abandoned station picks up a transmission that shouldn't be possible.",
                "Sci-Fi", 2024, 8.4, "125 min", "poster-scifi", false));

        movies.add(new Movie(11, "Iron Horizon",
                "A retired soldier is pulled back into action to protect a border town from a private militia.",
                "Action", 2022, 7.7, "120 min", "poster-action", false));

        movies.add(new Movie(12, "Broken Skyline",
                "Three strangers' lives collide during a citywide blackout that reveals more than any of them expected.",
                "Drama", 2023, 7.4, "100 min", "poster-drama", false));
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(int id) {
        return movies.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst();
    }

    public Movie getFeaturedMovie() {
        return movies.stream()
                .filter(Movie::isFeatured)
                .findFirst()
                .orElse(movies.get(0));
    }

    /** Simple "Trending Now" row: our highest-rated movies. */
    public List<Movie> getTrending() {
        return movies.stream()
                .filter(movie -> movie.getRating() >= 8.0)
                .toList();
    }

    /** Simple "Popular Movies" row: our newest releases. */
    public List<Movie> getPopular() {
        return movies.stream()
                .sorted(Comparator.comparingInt(Movie::getYear).reversed())
                .limit(6)
                .toList();
    }

    public List<Movie> getByGenre(String genre) {
        return movies.stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    /** Searches by title or genre, case-insensitive, using a simple "contains" match. */
    public List<Movie> searchMovies(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String lowerQuery = query.toLowerCase();
        return movies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(lowerQuery)
                        || movie.getGenre().toLowerCase().contains(lowerQuery))
                .toList();
    }

    public List<Movie> getMyList() {
        return myList;
    }

    public void addToMyList(int id) {
        Optional<Movie> movie = getMovieById(id);
        if (movie.isPresent() && !myList.contains(movie.get())) {
            myList.add(movie.get());
        }
    }
}
