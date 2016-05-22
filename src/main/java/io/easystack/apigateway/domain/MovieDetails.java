package io.easystack.apigateway.domain;

import io.easystack.apigateway.services.catalog.Genre;
import io.easystack.apigateway.services.reviews.Review;

import java.util.List;

public class MovieDetails {
    private String title;
    private String mlId;
    private List<Review> reviews;
    private List<Genre> genres;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMlId() {
        return mlId;
    }

    public void setMlId(String mlId) {
        this.mlId = mlId;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
