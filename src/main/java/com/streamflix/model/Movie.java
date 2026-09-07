package com.streamflix.model;

/**
 * A plain Java object representing one movie in the catalog.
 * There is no database - movies are just created in memory in MovieService.
 */
public class Movie {

    private int id;
    private String title;
    private String description;
    private String genre;
    private int year;
    private double rating;
    private String duration;
    private String image;
    private boolean featured;

    public Movie(int id, String title, String description, String genre, int year,
                 double rating, String duration, String image, boolean featured) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        this.duration = duration;
        this.image = image;
        this.featured = featured;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public boolean isFeatured() {
        return featured;
    }
}
