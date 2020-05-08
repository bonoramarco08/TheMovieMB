package com.example.themoviemb.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.themoviemb.R;
import com.example.themoviemb.ui.favorite.FavoriteFragment;
import com.example.themoviemb.ui.genere.GenereFragment;
import com.example.themoviemb.ui.home.HomeFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements FavoriteFragment.RemoveBadgeInterface, HomeFragment.AddOrCreateBadge , GenereFragment.AddOrCreateBadge {



    private Toolbar toolbar;
    private View navHome, navHeart , navGenere;
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
        navGenere = findViewById(R.id.navigation_genere);
        navView = findViewById(R.id.nav_view);



        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorite , R.id.navigation_genere)
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
        navGenere.setOnLongClickListener(view -> {
            createToast(getString(R.string.title_genere)).show();
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
    public void createOrAddBadge() {
        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_favorite);
        badge.setNumber(badge.getNumber() + 1);
        badge.setVisible(true);
    }

    @Override
    public void removeBadge() {
        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_favorite);
        if(badge.getNumber() == 1 || !badge.hasNumber()){
            navView.removeBadge(R.id.navigation_favorite);
        }else{
        badge.setNumber(badge.getNumber() - 1);
        badge.setVisible(true);}
    }

    @Override
    public void deleteBadge() {
        navView.removeBadge(R.id.navigation_favorite);
    }
}
