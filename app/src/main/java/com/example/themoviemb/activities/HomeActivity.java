package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity  {

private MovieProvider provider;
    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            Log.d("TAGGGGG", result.getResult().get(1).getTitle());
            for ( Movie movie : result.getResult()) {
                ContentValues contentValues = new ContentValues() ;
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

        BottomNavigationView navView = findViewById(R.id.nav_view);
        webService = WebService.getInstance();
        loadMovies();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorite)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }
    private void loadMovies() {
        if(new CursorLoader(getApplicationContext(), MovieProvider.MOVIES_URI, null, null, null, null).loadInBackground().getCount() == 0)
        webService.getMovies(webServerListener);
    }


}
