package com.example.themoviemb.data;

import android.provider.BaseColumns;
/**
 * classe che crea la tebella per i movie
 *imposto un _id autoincrement
 * il titolo
 * la descrizione
 * la foto di copertina
 * la foto per la descrizione
 * e l'id del film che restituisce api
 */
public class MovieTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "movie";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String COVER_PHOTO = "coverPhoto";
    public static final String DESCRIPTION_PHOTO = "descriptionPhoto";
    public static final String ID_FILM = "id_Film";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            COVER_PHOTO + " TEXT, " +
            ID_FILM + " INTEGER, " +
            DESCRIPTION_PHOTO + " TEXT ) ;" ;
}
