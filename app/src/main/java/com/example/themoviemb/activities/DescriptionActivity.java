package com.example.themoviemb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;

public class DescriptionActivity extends AppCompatActivity {

    private static int idMovie=-1;
    private ImageView descriptionImage;
    private TextView title, description;
    private MenuItem actionBarItem;
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
        idMovie=getIntent().getIntExtra("ID_MOVIE",-1);
        if(idMovie!=-1){
            Cursor movie = getContentResolver().query(Uri.parse(MovieProvider.MOVIES_URI+"/"+idMovie),null,null,null,null);
            movie.moveToNext();
            title.setText(movie.getString(movie.getColumnIndex(MovieTableHelper.TITLE)));
            description.setText(movie.getString(movie.getColumnIndexOrThrow(MovieTableHelper.DESCRIPTION)));
            Glide.with(getApplicationContext())
                    .load(movie.getString(movie.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)))
                    .into(descriptionImage);
            isFavorite=movie.getInt(movie.getColumnIndex(MovieTableHelper.IS_FAVORITE));
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
}
