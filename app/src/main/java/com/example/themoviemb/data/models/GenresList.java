package com.example.themoviemb.data.models;

import java.util.List;

public class GenresList{
List<Genres> genres;

    public List<Genres> getGenres() {
        return genres;
    }

    public void setGenres(List<Genres> genres) {
        this.genres = genres;
    }

    public GenresList(List<Genres> genres) {
        this.genres = genres;
    }
}
