package com.example.themoviemb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * calsse che crea il database con le 2 tabelle
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MovieTableHelper.CREATE);
        sqLiteDatabase.execSQL(FavoriteTableHelper.CREATE);
        sqLiteDatabase.execSQL(GenreTableHelper.CREATE);
        sqLiteDatabase.execSQL(GenreMovieTableHelper.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}