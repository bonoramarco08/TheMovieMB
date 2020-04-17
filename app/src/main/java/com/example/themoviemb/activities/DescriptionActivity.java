package com.example.themoviemb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieProvider;

public class DescriptionActivity extends AppCompatActivity {

    private static int idMovie=-1;
    ImageView descriptionImage;
    TextView title, description;
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


        }
    }
}
