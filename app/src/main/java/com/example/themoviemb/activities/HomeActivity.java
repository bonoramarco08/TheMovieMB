package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.example.themoviemb.R;
import com.example.themoviemb.VerificaInternet;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;
import com.example.themoviemb.ui.favorite.FavoriteFragment;
import com.example.themoviemb.ui.home.HomeFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements FavoriteFragment.RemoveBadgeInterface, HomeFragment.AddOrCreateBadge {



    private Toolbar toolbar;
    private View navHome, navHeart;
    private Toast toast;

    private BottomNavigationView navView;
    private NavController navController;
    private NavOptions navOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        navHome = findViewById(R.id.navigation_home);
        navHeart = findViewById(R.id.navigation_favorite);
        navView = findViewById(R.id.nav_view);



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

    @Override
    public void createOrAddBadge() {
        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_favorite);
        badge.setNumber(badge.getNumber() + 1);
        badge.setVisible(true);
    }

    @Override
    public void deleteBadge() {
        navView.removeBadge(R.id.navigation_favorite);
    }
}
