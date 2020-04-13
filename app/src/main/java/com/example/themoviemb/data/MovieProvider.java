package com.example.themoviemb.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    public static final String AUTORITY = "com.example.themoviemb.data.MovieProvider";
    public static final String BASE_PATH_MOVIES = "movies";
    public static final int ALL_MOVIE = 1;
    public static final int SINGLE_MOVIE = 0;
    public static final String MIME_TYPE_MOVIES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_movies";
    public static final String MIME_TYPE_MOVIE = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.single_movie";
    public static Uri MOVIES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_MOVIES);
    private MovieDatabaseHelper dBHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);



    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
