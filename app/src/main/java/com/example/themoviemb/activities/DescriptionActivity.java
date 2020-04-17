package com.example.themoviemb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;

public class DescriptionActivity extends AppCompatActivity {

    private static int idMovie=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        idMovie=getIntent().getIntExtra("ID_MOVIE",-1);
        if(idMovie!=-1){
            Cursor movie = getContentResolver().query(Uri.parse(MovieProvider.MOVIES_URI+"/"+idMovie),null,null,null,null);
            movie.moveToNext();

        }
    }
}
