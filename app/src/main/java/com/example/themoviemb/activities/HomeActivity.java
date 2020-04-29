package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;
import com.example.themoviemb.VerificaInternet;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;
import com.example.themoviemb.ui.favorite.FavoriteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity{


    private WebService webService;
    private Toolbar toolbar;
    private View navHome, navHeart;
    private Toast toast;

    private BottomNavigationView navView;
    private NavController navController;
    private NavOptions navOptions;
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
        navView = findViewById(R.id.nav_view);
        webService = WebService.getInstance();
        loadMovies();


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorite)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
                if (VerificaInternet.getConnectivityStatusString(getBaseContext())) {
                    webService.getMoviesPage(webServerListener, 1, getString(R.string.lingua));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialog);

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
