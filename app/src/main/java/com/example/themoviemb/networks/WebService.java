package com.example.themoviemb.networks;

import android.util.Log;

import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.interface_movie.MovieService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

        private String TODO_BASE_URL = "https://api.themoviedb.org/3/movie/";
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

    }