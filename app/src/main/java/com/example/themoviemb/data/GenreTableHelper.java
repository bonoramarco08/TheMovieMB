package com.example.themoviemb.data;

import android.provider.BaseColumns;

public class GenreTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "genre";
    public static final String ID_GENRE = "id_genre";
    public static final String TEXT_GENRE = "textGenre";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_GENRE + " INTEGER, " +
            TEXT_GENRE + " TEXT ) ;" ;
}
