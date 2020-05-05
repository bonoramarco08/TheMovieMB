package com.example.themoviemb.data;

import android.provider.BaseColumns;

public class GenreMovieTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "genreformovie";
    public static final String ID_MOVIE = "id_movie";
    public static final String ID_GENRE = "id_genre";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_MOVIE + " INTEGER, " +
            ID_GENRE + " INTEGER ) ;" ;
}
