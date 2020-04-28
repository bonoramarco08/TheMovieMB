package com.example.themoviemb.data.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Movie {

    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("id")
    private String idFilm;
    private String _id;


    public Movie() {

    }

    public Movie(String title, String backdropPath, String overview, String posterPath, String idFilm, String _id) {
        this.title = title;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.posterPath = posterPath;
        this.idFilm = idFilm;
        this._id = _id;
    }

    public Movie(String title, String backdropPath, String overview, String posterPath, String _id) {
        this.title = title;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.posterPath = posterPath;
        this._id = _id;
    }
    public String getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(String idFilm) {
        this.idFilm = idFilm;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        if (backdropPath != null && !backdropPath.isEmpty()) {
            if(!backdropPath.toLowerCase().contains("https://")) {
                if (!backdropPath.toLowerCase().contains("http://")) {
                    return "https://image.tmdb.org/t/p/original" + backdropPath;
                } else {
                    return backdropPath;
                }
            }else
            {
                return backdropPath;
            }
        }
        return null;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Nullable
    public String getPosterPath() {
        if (posterPath != null && !posterPath.isEmpty()) {
            if (!posterPath.toLowerCase().contains("https://")) {
                if (!posterPath.toLowerCase().contains("http://")) {
                    return "https://image.tmdb.org/t/p/w342" + posterPath;
                } else {
                    return posterPath;
                }
            } else {
                return posterPath;
            }
        }
        return null;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

}