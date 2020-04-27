package com.example.themoviemb.networks;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.interface_movie.MovieService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.example.themoviemb.R;
import android.app.Activity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.themoviemb.R;
public class WebService {

        private String TODO_BASE_URL = "https://api.themoviedb.org/";
        private static WebService instance;
        private MovieService movieService;

        private WebService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TODO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            movieService = retrofit.create(MovieService.class);
        }

        public static WebService getInstance() {
            if (instance == null)
                instance = new WebService();
            return instance;
        }

        public void getMovies(final IWebServer callback) {
            Call<Result> moviesrequest = movieService.getMovies();
            moviesrequest.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.code() == 200) {
                        callback.onMoviesFetched(true, response.body(), -1, null);
                    } else {
                        try {
                            callback.onMoviesFetched(true, null, response.code(), response.errorBody().string());
                        } catch (IOException ex) {
                            Log.e("WebService", ex.toString());
                            callback.onMoviesFetched(true, null, response.code(), "Generic error message");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    callback.onMoviesFetched(false, null, -1, t.getLocalizedMessage());

                }

            });
        }
    public void getMoviesPage(final IWebServer callback , int page , String lingua) {
        Call<Result> moviesrequest = movieService.getMoviesPage(page,getReleaseDate(),lingua);
        moviesrequest.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                    callback.onMoviesFetched(true, response.body(), -1, null);
                } else {
                    try {
                        callback.onMoviesFetched(true, null, response.code(), response.errorBody().string());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString());
                        callback.onMoviesFetched(true, null, response.code(), "Generic error message");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callback.onMoviesFetched(false, null, -1, t.getLocalizedMessage());

            }

        });
    }
    public String getReleaseDate() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        return format1.format(cal.getTime());
    }

    }