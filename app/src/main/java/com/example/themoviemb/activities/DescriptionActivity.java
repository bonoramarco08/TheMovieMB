package com.example.themoviemb.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.themoviemb.R;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.GenreMovieTableHelper;
import com.example.themoviemb.data.GenreTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;

import java.util.ArrayList;


public class DescriptionActivity extends AppCompatActivity {

    private static int idMovie = -1;
    private ImageView descriptionImage;
    private TextView title, description ,generi;
    private ImageButton btnBack;
    private ImageButton btnHeart;
    private Bitmap resourceImageDescription;
    private int isFavorite;
    private long vibranceHeart;
    private LottieAnimationView lottieAnimationView;
    private boolean isDarkMode;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        setNightMode();
        setViews();
        startAnimation();

        idMovie = getIntent().getIntExtra("ID_MOVIE", -1);
        if (idMovie != -1) {
            Cursor movie = getContentResolver().query(MovieProvider.JOIN_URI, null, FavoriteTableHelper.ID_MOVIE + " = " + idMovie, null, null);
            movie.moveToNext();
            title.setText(movie.getString(movie.getColumnIndex(MovieTableHelper.TITLE)));
            description.setText(movie.getString(movie.getColumnIndexOrThrow(MovieTableHelper.DESCRIPTION)));
            if(description.getText().toString().equals("")){
                description.setText(getString(R.string.descriptoinull));
            }
            onHeartAppear();
            downloadImages(movie);
            isFavorite = movie.getInt(movie.getColumnIndex(FavoriteTableHelper.IS_FAVORITE));
            btnBack.setOnClickListener(view -> finish());
            descriptionImage.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int lengthBtnBack = btnBack.getWidth() * btnBack.getHeight();

                            if (resourceImageDescription != null) {
                                if (lengthBtnBack != 0) {
                                    long vibrance = sumVibranceImageDescription(resourceImageDescription, btnBack) / lengthBtnBack;
                                    setColorImage(vibrance);

                                }
                            }
                            //obbligatorio rimuovere il listener
                            descriptionImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
            Cursor genre = getContentResolver().query(MovieProvider.ALL_GENRE_JOIN_URI, null, GenreMovieTableHelper.ID_MOVIE +" = " + idMovie ,null, null);
            String s =   "";
            for(genre.moveToFirst();!genre.isAfterLast();genre.moveToNext()){
                    s = s +"&#8226;" + genre.getString(genre.getColumnIndex(GenreTableHelper.TEXT_GENRE)) + "<br>";
            }
            Log.d("TAGG", s + genre.getCount());
            generi.setText(Html.fromHtml(s,1));
            }
        btnHeart.setOnClickListener(view -> {
            onHeartChange();
        });

        //quando attivo la dark mode, setto un listener che verifica questo evento in modo da cambiare il colore dell'icona in landscape
        btnBack.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE&&isDarkMode)
                        btnBack.setImageResource(R.drawable.left_arrow_white);
                    else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE&&!isDarkMode)
                        btnBack.setImageResource(R.drawable.left_arrow_black);
                });
    }


    //setta la freccia in base al valore vibrance
    private void setColorImage(long vibrance) {
        if (vibrance >= 50)
            btnBack.setImageResource(R.drawable.left_arrow_black);
        else
            btnBack.setImageResource(R.drawable.left_arrow_white);

    }

    private void downloadImages(Cursor movie){
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(movie.getString(movie.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        descriptionImage.setImageBitmap(resource);
                        lottieAnimationView.setVisibility(View.GONE);
                        //al primo giro funziona, ma se riapro la stessa descrizione la seconda volta non mi ritorna i pixel del btnBack
                        //quindi se succede questo, ho creato un viewTreeObserver sotto che si prende le risorse Bitmap dell'immagine
                        resourceImageDescription = resource;
                        int lengthArrow = btnBack.getWidth() * btnBack.getHeight();
                        int lengthHeart = btnHeart.getWidth() * btnHeart.getHeight();
                        //se è 0, richiamerà il metodo con il viewTreeObserver
                        if (lengthArrow != 0) {
                            long vibrance = sumVibranceImageDescription(resourceImageDescription, btnBack) / lengthArrow;
                            setColorImage(vibrance);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        descriptionImage.setImageResource(R.drawable.error_image);
                    }
                });
    }

    private void setNightMode(){
        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = true;
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = false;
                break;
        }
    }
    private void setViews(){
        descriptionImage = findViewById(R.id.imgDescription);
        title = findViewById(R.id.txtTitle);
        description = findViewById(R.id.txtDescription);
        btnBack = findViewById(R.id.btnBack);
        btnHeart = findViewById(R.id.btnHeart);
        lottieAnimationView = findViewById(R.id.imageProgress);
        generi = findViewById(R.id.generi);
    }

    public void onHeartAppear() {
        if (idMovie != -1) {
            Cursor movie = getContentResolver().query(MovieProvider.JOIN_URI, null, FavoriteTableHelper.ID_MOVIE + " = " + idMovie, null, null);
            movie.moveToNext();
            if (movie.getInt(movie.getColumnIndex(FavoriteTableHelper.IS_FAVORITE)) == 0)
                if (!isDarkMode)
                    btnHeart.setImageResource(R.drawable.unfavorite);
                else
                    btnHeart.setImageResource(R.drawable.unfavorite_white);
            else if (!isDarkMode)
                btnHeart.setImageResource(R.drawable.favorite);
            else
                btnHeart.setImageResource(R.drawable.favorite_white);
        }
    }

    public void onHeartChange() {
        if (idMovie != -1) {
            Cursor movie = getContentResolver().query(MovieProvider.JOIN_URI, null, FavoriteTableHelper.ID_MOVIE + " = " + idMovie, null, null);
            movie.moveToNext();
            ContentValues contentValues = new ContentValues();

            if (movie.getInt(movie.getColumnIndex(FavoriteTableHelper.IS_FAVORITE)) == 0) {
                contentValues.put(FavoriteTableHelper.IS_FAVORITE, 1);
                getContentResolver().update(Uri.parse(MovieProvider.FAVORITE_URI + "/" + idMovie), contentValues, FavoriteTableHelper.ID_MOVIE + " = " + idMovie, null);
                if (!isDarkMode)
                    btnHeart.setImageResource(R.drawable.favorite);
                else
                    btnHeart.setImageResource(R.drawable.favorite_white);
                Toast.makeText(this, getString(R.string.toastagg), Toast.LENGTH_SHORT).show();
            } else {
                contentValues.put(FavoriteTableHelper.IS_FAVORITE, 0);
                getContentResolver().update(MovieProvider.FAVORITE_URI, contentValues, FavoriteTableHelper.ID_MOVIE + " = " + idMovie, null);
                if (!isDarkMode)
                    btnHeart.setImageResource(R.drawable.unfavorite);
                else
                    btnHeart.setImageResource(R.drawable.unfavorite_white);
                Toast.makeText(this, getString(R.string.toastrem), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //mi prendo la proprietà vibrance di ogni pixel dell'immagine, i pixel solo dove si trova il bottone BACK
    private long sumVibranceImageDescription(Bitmap resource, ImageButton btn) {


        ArrayList<Integer> pixels;
        long sumV = 0;
        //aggiungo all'arraylist tutti i pixel
        pixels = addPixels(resource, btn.getWidth(), btn.getHeight(), btn.getX(), btn.getY());

        //per ogni pixel aggiungo la percentuale di vibrance di ogni pixel
        for (int p : pixels) {
            float[] hsv = new float[3];
            int r = Color.red(p);
            int g = Color.green(p);
            int b = Color.blue(p);
            Color.RGBToHSV(r, g, b, hsv);
            sumV += hsv[2] * 100;
        }
        return sumV;
    }

    private ArrayList<Integer> addPixels(Bitmap resource, int width, int height, float x, float y) {
        ArrayList<Integer> pixels = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels.add(resource.getPixel((int) x + i, (int) y + j));
            }
        }
        return pixels;
    }

    private void startAnimation() {
        lottieAnimationView.setSpeed(2.0F); // How fast does the animation play
        lottieAnimationView.setProgress(50F); // Starts the animation from 50% of the beginning
    }

    private void stopAnimation() {
        lottieAnimationView.cancelAnimation(); // Cancels the animation
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
