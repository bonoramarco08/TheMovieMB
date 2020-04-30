package com.example.themoviemb.data.models;

import java.util.List;

/**
 * classe del risultato della chiamata api , che contiene la lista di film
 */
public class Result {

    private List<Movie> results;
    private int page;

    public Result(List<Movie> results, int page) {
        this.results = results;
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResult() {
        return results;
    }

    public void setResult(List<Movie> result) {
        this.results = result;
    }

}
