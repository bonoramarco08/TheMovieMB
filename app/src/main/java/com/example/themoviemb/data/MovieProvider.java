package com.example.themoviemb.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
    static {
        uriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES, ALL_MOVIE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES + "/#", SINGLE_MOVIE);
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase vDb = dBHelper.getReadableDatabase();
        SQLiteQueryBuilder vBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                vBuilder.setTables(MovieTableHelper.TABLE_NAME);
                vBuilder.appendWhere(MovieTableHelper._ID + " = " + uri.getLastPathSegment());
                break;
            case ALL_MOVIE:
                vBuilder.setTables(MovieTableHelper.TABLE_NAME);
                break;
        }

        Cursor vCursor = vBuilder.query(vDb, strings, s, strings1, null, null, s1);
        vCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return vCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                return MIME_TYPE_MOVIE;

            case ALL_MOVIE:
                return MIME_TYPE_MOVIES;

        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (uriMatcher.match(uri) == ALL_MOVIE) {
            SQLiteDatabase vDb = dBHelper.getWritableDatabase();
            long vResult = vDb.insert(MovieTableHelper.TABLE_NAME, null, contentValues);
            String vResultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_MOVIES + "/" + vResult;
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(vResultString);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        String vTableName = "";
        String vQuery = "";
        SQLiteDatabase vDb = dBHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = s;
                break;

            case SINGLE_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = MovieTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;
        }
        int vDeleteRows = vDb.delete(vTableName, vQuery, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return vDeleteRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        String vTableName = "";
        String vQuery = "";
        SQLiteDatabase vDb = dBHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = s;
                break;
            case SINGLE_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = MovieTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;
        }
        int vUpdateRows = vDb.update(vTableName, contentValues, vQuery, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return vUpdateRows;
    }
}
