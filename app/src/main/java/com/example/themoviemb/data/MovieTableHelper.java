package com.example.themoviemb.data;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "movie";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String COVER_PHOTO = "coverPhoto";
    public static final String DESCRIPTION_PHOTO = "descriptionPhoto";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            COVER_PHOTO + " TEXT, " +
            DESCRIPTION_PHOTO + " TEXT ) ;" ;
}
