package com.example.themoviemb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT= 1500;
    LottieAnimationView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash=findViewById(R.id.splash);

        startAnimation();
        new Handler().postDelayed(() -> {
            Intent intent=new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        },SPLASH_TIME_OUT);
    }

    private void startAnimation() {
        splash.playAnimation();
        splash.setSpeed(2F); // How fast does the animation play
        splash.setProgress(0F); // Starts the animation from 50% of the beginning
    }
}
