package com.example.themoviemb.data;

import android.provider.BaseColumns;

/**
 * tabella dei film preferiti
 * imposto un _id AUTOINCREMENT
 * l'id del film
 * e imposto  0 se non è tra i preferiti e 1 se lo è
 */
public class FavoriteTableHelper  implements BaseColumns {
    public static final String TABLE_NAME = "favorite";
    public static final String ID_MOVIE = "id_movie";
    public static final String IS_FAVORITE = "isFavorite";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ID_MOVIE + " INTEGER, " +
            IS_FAVORITE + " INTEGER ) ;" ;
}
