package com.example.themoviemb.networks;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.themoviemb.data.models.GenresList;
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

/**
 * classe per le chiamate api
 */
public class WebService {
    /**
     * url di partenza per tutte le chiamate api
     */
        private String TODO_BASE_URL = "https://api.themoviedb.org/";
        private static WebService instance;
        private MovieService movieService;

    /**
     * Costruttore della classe webService
     * implemente retrofit per le chiamte
     */
        private WebService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TODO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            movieService = retrofit.create(MovieService.class);
        }

    /**
     *  Metodo per rendere la classe un Singleton
     *  controlla che l'istanza della classe se esiste o no
     * @return restituisce un instanza della classe WebService
     */
        public static WebService getInstance() {
            if (instance == null)
                instance = new WebService();
            return instance;
        }

    /**
     * motodo per la chiamate api con le pagine
     * controlla che la chiamata sia andata a buon fine e richiama l'interfaccia per la rispsota
     * @param callback passo l'interfacca per la risposta
     * @param page passo il numero della pagina da cercare
     * @param lingua passo la lingua dell'utente per prendere i film coerentemente
     */
    public void getMoviesPage(final IWebServer callback , int page , String lingua) {
        Call<Result> moviesrequest = movieService.getMoviesPage(page,getReleaseDate(),lingua);
        moviesrequest.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                /**
                 * chiamata andata a buon fine
                 */
                if (response.code() == 200) {
                    callback.onMoviesFetched(true, response.body(), -1, null);
                } else {
                    try {
                        /**
                         * chiamata non andata a  buon fine
                         */
                        callback.onMoviesFetched(true, null, response.code(), response.errorBody().string());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString());
                        /**
                         * errore nella chiamata
                         */
                        callback.onMoviesFetched(true, null, response.code(), "Generic error message");
                    }
                }
            }
            /**
             * in caso la chiamta falisca
             */
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callback.onMoviesFetched(false, null, -1, t.getLocalizedMessage());

            }

        });
    }
    public void getGenres(final IWebServer callback , String lingua) {
        Call<GenresList> moviesrequest = movieService.getGenres(lingua);
        moviesrequest.enqueue(new Callback<GenresList>() {
            @Override
            public void onResponse(Call<GenresList> call, Response<GenresList> response) {
                /**
                 * chiamata andata a buon fine
                 */
                if (response.code() == 200) {
                    callback.onGeneresFetched(true, response.body(), -1, null);
                } else {
                    try {
                        /**
                         * chiamata non andata a  buon fine
                         */
                        callback.onGeneresFetched(true, null, response.code(), response.errorBody().string());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString());
                        /**
                         * errore nella chiamata
                         */
                        callback.onGeneresFetched(true, null, response.code(), "Generic error message");
                    }
                }
            }
            /**
             * in caso la chiamta falisca
             */
            @Override
            public void onFailure(Call<GenresList> call, Throwable t) {
                callback.onGeneresFetched(false, null, -1, t.getLocalizedMessage());

            }

        });
    }

    /**
     * metodo che restituisce la data corrente da passare all'api
     * @return la data corrente in formato legibile per api
     */
    public String getReleaseDate() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        return format1.format(cal.getTime());
    }

    }