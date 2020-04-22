package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements DialogFavorite.IFavoritDialog {


    private WebService webService;
    private Toolbar toolbar;
    private View navHome, navHeart;
    private Toast toast;
    private LottieAnimationView lottieAnimationView;
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
                    getContentResolver().insert(MovieProvider.MOVIES_URI, contentValues);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        navHome = findViewById(R.id.navigation_home);
        navHeart = findViewById(R.id.navigation_favorite);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        webService = WebService.getInstance();
        loadMovies();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorite)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navHome.setOnLongClickListener(view -> {
            createToast(getString(R.string.title_home)).show();
            return true;
        });
        navHeart.setOnLongClickListener(view -> {
            createToast(getString(R.string.title_favorite)).show();
            return true;
        });


    }

    private Toast createToast(String textToShow) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(HomeActivity.this, textToShow, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        return toast;
    }

    private void loadMovies() {
        try {
            Cursor cursor = new CursorLoader(getApplicationContext(), MovieProvider.MOVIES_URI, null, null, null, null).loadInBackground();
            if (cursor.getCount() == 0)
                webService.getMoviesPage(webServerListener, 1);
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }

    }


    @Override
    public void onResponse(boolean aResponse, long aId, Boolean isRemoved) {
        if (aResponse) {
            if (!isRemoved) {
                ContentValues cv = new ContentValues();
                cv.put(MovieTableHelper.IS_FAVORITE, 1);
                getContentResolver().update(MovieProvider.MOVIES_URI, cv, MovieTableHelper._ID + " = " + aId, null);
                startAnimation();
            } else {
                ContentValues cv = new ContentValues();
                cv.put(MovieTableHelper.IS_FAVORITE, 0);
                getContentResolver().update(MovieProvider.MOVIES_URI, cv, MovieTableHelper._ID + " = " + aId, null);
            }
        }
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

    private void startAnimation(){
        lottieAnimationView.playAnimation();
        lottieAnimationView.setSpeed(2.0F); // How fast does the animation play
        lottieAnimationView.setProgress(0F); // Starts the animation from 50% of the beginning
    }

    private void stopAnimation(){
        lottieAnimationView.cancelAnimation(); // Cancels the animation
    }
}
