package com.example.themoviemb.interface_movie;

import com.example.themoviemb.data.models.Result;

import java.util.List;

public interface IWebServer {
    void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage);
}