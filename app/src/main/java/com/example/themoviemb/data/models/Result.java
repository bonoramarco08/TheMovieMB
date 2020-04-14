package com.example.themoviemb.data.models;

import java.util.List;

public class Result {

    private List<Movie> results;

    public List<Movie> getResult() {
        return results;
    }

    public void setResult(List<Movie> result) {
        this.results = result;
    }

    public Result(List<Movie> result) {
        this.results = result;
    }
}
