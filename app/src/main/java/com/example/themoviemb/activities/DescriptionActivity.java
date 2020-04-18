package com.example.themoviemb.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;

public class DescriptionActivity extends AppCompatActivity {

    private static int idMovie=-1;
    private ImageView descriptionImage;
    private TextView title, description;
    private MenuItem actionBarItem;
    private ImageButton btnBack;
    private int isFavorite;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_descr, menu);
        actionBarItem = menu.findItem(R.id.action_cart);
        if(isFavorite==1){
            actionBarItem.setIcon(getDrawable(R.drawable.favorite));
        }else{
            actionBarItem.setIcon(getDrawable(R.drawable.unfavorite));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        descriptionImage=findViewById(R.id.imgDescription);
        title=findViewById(R.id.txtTitle);
        description=findViewById(R.id.txtDescription);
        btnBack=findViewById(R.id.btnBack);
        setPropertiesImage();


        idMovie=getIntent().getIntExtra("ID_MOVIE",-1);
        if(idMovie!=-1){
            Cursor movie = getContentResolver().query(Uri.parse(MovieProvider.MOVIES_URI+"/"+idMovie),null,null,null,null);
            movie.moveToNext();
            title.setText(movie.getString(movie.getColumnIndex(MovieTableHelper.TITLE)));
            description.setText(movie.getString(movie.getColumnIndexOrThrow(MovieTableHelper.DESCRIPTION)));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(movie.getString(movie.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            descriptionImage.setImageBitmap(resource);
                            int lengthArrow = btnBack.getWidth()*btnBack.getHeight();
                            setBtnBackColor(sumVibranceImageDescription(resource),lengthArrow);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
            isFavorite=movie.getInt(movie.getColumnIndex(MovieTableHelper.IS_FAVORITE));

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    public void onFavoriteChange(MenuItem item){
        if(idMovie!=-1) {
            Cursor movie = getContentResolver().query(Uri.parse(MovieProvider.MOVIES_URI + "/" + idMovie), null, null, null, null);
            movie.moveToNext();
            ContentValues contentValues=new ContentValues();

            if(movie.getInt(movie.getColumnIndex(MovieTableHelper.IS_FAVORITE))==0){
                contentValues.put(MovieTableHelper.IS_FAVORITE,1);
                getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI+"/"+idMovie),contentValues,null,null);
                item.setIcon(getDrawable(R.drawable.favorite));
                Toast.makeText(this, "Aggiunto ai preferiti", Toast.LENGTH_LONG).show();
            }
            else{
                contentValues.put(MovieTableHelper.IS_FAVORITE,0);
                getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI+"/"+idMovie),contentValues,null,null);
                item.setIcon(getDrawable(R.drawable.unfavorite));
                Toast.makeText(this, "Rimosso dai preferiti", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setPropertiesImage(){
        descriptionImage.setDrawingCacheEnabled(true);
        descriptionImage.buildDrawingCache(true);
    }


    //mi prendo la proprietà vibrance di ogni pixel dell'immagine, i pixel solo dove si trova il bottone BACK
    private long sumVibranceImageDescription(Bitmap resource){


        ArrayList<Integer>pixels = new ArrayList();
        long sumV=0;
        //aggiungo all'arraylist tutti i pixel
        for(int i=0; i<btnBack.getWidth();i++){
            for(int j=0; j<btnBack.getHeight();j++){
                pixels.add(resource.getPixel((int)btnBack.getX()+i,(int)btnBack.getY()+j));
            }
        }
        //per ogni pixel aggiungo la percentuale di vibrance di ogni pixel
        for (int p:pixels) {
            float[] hsv = new float[3];
            int r=Color.red(p);
            int g=Color.green(p);
            int b=Color.blue(p);
            Color.RGBToHSV(r,g,b,hsv);
            sumV+=hsv[2]*100;
        }


        return sumV;
    }

    private void setBtnBackColor(long vibrance,int length){
        if(vibrance/length>=50){
            btnBack.setImageResource(R.drawable.left_arrow_black);
        }else{
            btnBack.setImageResource(R.drawable.left_arrow_white);
        }
    }

    private void setBtnHeartColor(){

    }


}
