package com.example.themoviemb.data;

import android.provider.BaseColumns;

public class FavoriteTableHelper  implements BaseColumns {
    public static final String TABLE_NAME = "favorite";
    public static final String ID_MOVIE = "id_movie";
    public static final String IS_FAVORITE = "isFavorite";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_MOVIE + " INTEGER, " +
            IS_FAVORITE + " INTEGER ) ;" ;
}
