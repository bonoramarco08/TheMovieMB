package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieDatabaseHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.interface_movie.MovieService;
import com.example.themoviemb.networks.WebService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements DialogFavorite.IFavoritDialog {


    private WebService webService;
    private Toolbar toolbar;
    private MenuItem actionBarItem;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            Log.d("TAGGGGG", result.getResult().get(1).getTitle());
            for (Movie movie : result.getResult()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieTableHelper.TITLE, movie.getTitle());
                contentValues.put(MovieTableHelper.COVER_PHOTO, movie.getPosterPath());
                contentValues.put(MovieTableHelper.DESCRIPTION, movie.getOverview());
                contentValues.put(MovieTableHelper.DESCRIPTION_PHOTO, movie.getBackdropPath());
                getContentResolver().insert(MovieProvider.MOVIES_URI, contentValues);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       toolbar = findViewById(R.id.toolbarHome);
       setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        webService = WebService.getInstance();
        loadMovies();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorite)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }



    private void loadMovies() {
        try {
            Cursor cursor = new CursorLoader(getApplicationContext(), MovieProvider.MOVIES_URI, null, null, null, null).loadInBackground();
            if (cursor.getCount() == 0)
                webService.getMovies(webServerListener);
            else
            {
                cursor.moveToPosition(cursor.getCount() - 1);
                Toast.makeText(getBaseContext(), cursor.getInt(cursor.getColumnIndex(MovieTableHelper.PAGE))  +"", Toast.LENGTH_SHORT).show();
             //   webService.getMoviesPage(webServerListener, cursor.getInt(cursor.getColumnIndex(MovieTableHelper.PAGE)) , MovieService.SortBy.RELEASE_DATE_DESCENDING);
            }
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
}
