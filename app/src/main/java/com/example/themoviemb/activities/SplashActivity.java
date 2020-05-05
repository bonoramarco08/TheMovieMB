package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.loader.content.CursorLoader;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;
import com.example.themoviemb.VerificaInternet;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.GenreMovieTableHelper;
import com.example.themoviemb.data.GenreTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Genres;
import com.example.themoviemb.data.models.GenresList;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;
    LottieAnimationView splash;
    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            if (result != null) {
                for (Movie movie : result.getResult()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieTableHelper.TITLE, movie.getTitle());
                    contentValues.put(MovieTableHelper.COVER_PHOTO, movie.getPosterPath());
                    contentValues.put(MovieTableHelper.DESCRIPTION, movie.getOverview());
                    contentValues.put(MovieTableHelper.DESCRIPTION_PHOTO, movie.getBackdropPath());
                    contentValues.put(MovieTableHelper.ID_FILM, movie.getIdFilm());
                    Uri r = getContentResolver().insert(MovieProvider.MOVIES_URI, contentValues);

                    long id = Long.parseLong(r.getLastPathSegment());
                    ContentValues contentValuesFavorite = new ContentValues();
                    contentValuesFavorite.put(FavoriteTableHelper.ID_MOVIE, id);
                    contentValuesFavorite.put(FavoriteTableHelper.IS_FAVORITE, 0);
                    getContentResolver().insert(MovieProvider.FAVORITE_URI, contentValuesFavorite);

                    for (int i = 0 ; i<movie.getGenres().length ; i++){
                        ContentValues contentValuesGenre = new ContentValues();
                        contentValuesGenre.put(GenreMovieTableHelper.ID_MOVIE, id);
                        contentValuesGenre.put(GenreMovieTableHelper.ID_GENRE, movie.getGenres()[i]);
                        getContentResolver().insert(MovieProvider.GENREMOVIE_URI, contentValuesGenre);
                    }
                }
            }
        }

        @Override
        public void onGeneresFetched(boolean success, GenresList result, int errorCode, String errorMessage) {
            if (result != null) {
                for (Genres genre : result.getGenres()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(GenreTableHelper.ID_GENRE, genre.getId());
                    contentValues.put(GenreTableHelper.TEXT_GENRE, genre.getName());
                    Uri r = getContentResolver().insert(MovieProvider.GENRE_URI, contentValues);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.splash);

        startAnimation();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
        webService = WebService.getInstance();
        loadMovies();
    }

    private void loadMovies() {
        try {
            Cursor cursor = new CursorLoader(getApplicationContext(), MovieProvider.GENRE_URI, null, null, null, null).loadInBackground();
            if (cursor.getCount() == 0)
                if (VerificaInternet.getConnectivityStatusString(getBaseContext())) {
                    webService.getMoviesPage(webServerListener, 1, getString(R.string.lingua));
                    webService.getGenres(webServerListener,getString(R.string.lingua));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this, R.style.MyDialog);

                    builder.setMessage(R.string.dialog_message)
                            .setTitle(R.string.dialog_title)
                            .setNeutralButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }

    }

    private void startAnimation() {
        splash.playAnimation();
        splash.setSpeed(2F); // How fast does the animation play
        splash.setProgress(0F); // Starts the animation from 50% of the beginning
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
