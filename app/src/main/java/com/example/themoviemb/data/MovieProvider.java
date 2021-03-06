package com.example.themoviemb.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * provider del database
 */
public class MovieProvider extends ContentProvider {

    public static final String AUTORITY = "com.example.themoviemb.data.MovieProvider";
    //movie table provider
    public static final String BASE_PATH_MOVIES = "movies";
    public static final int ALL_MOVIE = 1;
    public static final int SINGLE_MOVIE = 0;
    public static final String MIME_TYPE_MOVIES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_movies";
    public static final String MIME_TYPE_MOVIE = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.single_movie";
    public static Uri MOVIES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_MOVIES);
    //favorite table provider
    public static final String BASE_PATH_FAVORITES = "favorites";
    public static final int ALL_FAVORITE = 2;
    public static final int SINGLE_FAVORITE = 3;
    public static final String MIME_TYPE_FAVORITES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_favorites";
    public static final String MIME_TYPE_FAVORITE = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.single_favorite";
    public static Uri FAVORITE_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_FAVORITES);
    //common provider
    private MovieDatabaseHelper dBHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String BASE_PATH_JOIN = "join";
    public static final String MIME_TYPE_JOINS = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_joins";
    public static Uri JOIN_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_JOIN);
    public static final int ALL_JOIN = 6;
    public static final String BASE_PATH_GENRE = "genre";
    public static final String MIME_TYPE_GENRES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_genres";
    public static Uri GENRE_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_GENRE);
    public static final int ALL_GENRE = 7;
    public static final int ADD_GENERE_FOR_FILM = 8;
    public static final String BASE_PATH_GENRE_MOVIE = "genreMovie";
    public static Uri GENREMOVIE_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_GENRE_MOVIE);
    public static final int ALL_GENRE_JOIN =9;
    public static final String BASE_PATHALL_GENRE_JOIN = "all_genre_join";
    public static Uri ALL_GENRE_JOIN_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATHALL_GENRE_JOIN);

    static {
        uriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES, ALL_MOVIE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES + "/#", SINGLE_MOVIE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_FAVORITES, ALL_FAVORITE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_FAVORITES + "/#", SINGLE_FAVORITE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_JOIN,ALL_JOIN);
        uriMatcher.addURI(AUTORITY, BASE_PATH_GENRE,ALL_GENRE);
        uriMatcher.addURI(AUTORITY, BASE_PATH_GENRE_MOVIE,ADD_GENERE_FOR_FILM);
        uriMatcher.addURI(AUTORITY, BASE_PATHALL_GENRE_JOIN,ALL_GENRE_JOIN);
    }


    @Override
    public boolean onCreate() {
        dBHelper = new MovieDatabaseHelper(getContext());
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
            case SINGLE_FAVORITE:
                vBuilder.setTables(FavoriteTableHelper.TABLE_NAME);
                vBuilder.appendWhere(MovieTableHelper._ID + " = " + uri.getLastPathSegment());
                break;
            case ALL_FAVORITE:
                vBuilder.setTables(FavoriteTableHelper.TABLE_NAME);
                break;
            case ALL_JOIN:
                vBuilder.setTables(MovieTableHelper.TABLE_NAME +" LEFT OUTER JOIN "+ FavoriteTableHelper.TABLE_NAME+" ON ("+MovieTableHelper.TABLE_NAME+"."+ MovieTableHelper._ID+" = "+FavoriteTableHelper.TABLE_NAME+"."+ FavoriteTableHelper.ID_MOVIE+")");
                break;
            case ALL_GENRE:
                vBuilder.setTables(GenreTableHelper.TABLE_NAME);
                break;
            case ADD_GENERE_FOR_FILM:
                vBuilder.setTables(GenreMovieTableHelper.TABLE_NAME);
                break;
            case ALL_GENRE_JOIN:
                vBuilder.setTables(GenreMovieTableHelper.TABLE_NAME +" LEFT OUTER JOIN "+ GenreTableHelper.TABLE_NAME+" ON ("+GenreMovieTableHelper.TABLE_NAME+"."+ GenreMovieTableHelper.ID_GENRE+" = "+GenreTableHelper.TABLE_NAME+"."+ GenreTableHelper.ID_GENRE+")");
                break;
        }

        Cursor vCursor = vBuilder.query(vDb, strings, s, strings1, null, null, s1);
        try {
            vCursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }
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
            case SINGLE_FAVORITE:
                return MIME_TYPE_FAVORITE;
            case ALL_FAVORITE:
                return MIME_TYPE_FAVORITES;
            case ALL_JOIN:
                return MIME_TYPE_JOINS;
            case ALL_GENRE:
                return  MIME_TYPE_GENRES;

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
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d("Error", e.getMessage());
            }
            return Uri.parse(vResultString);
        }
        if (uriMatcher.match(uri) == ALL_FAVORITE) {
            SQLiteDatabase vDb = dBHelper.getWritableDatabase();
            long vResult = vDb.insert(FavoriteTableHelper.TABLE_NAME, null, contentValues);
            String vResultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_FAVORITES + "/" + vResult;
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d("Error", e.getMessage());
            }
            return Uri.parse(vResultString);
        }
        if (uriMatcher.match(uri) == ALL_GENRE) {
            SQLiteDatabase vDb = dBHelper.getWritableDatabase();
            long vResult = vDb.insert(GenreTableHelper.TABLE_NAME, null, contentValues);
            String vResultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_FAVORITES + "/" + vResult;
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d("Error", e.getMessage());
            }
            return Uri.parse(vResultString);
        }
        if (uriMatcher.match(uri) == ADD_GENERE_FOR_FILM) {
            SQLiteDatabase vDb = dBHelper.getWritableDatabase();
            long vResult = vDb.insert(GenreMovieTableHelper.TABLE_NAME, null, contentValues);
            String vResultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_GENRE_MOVIE + "/" + vResult;
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.d("Error", e.getMessage());
            }
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
            case ALL_FAVORITE:
                vTableName = FavoriteTableHelper.TABLE_NAME;
                vQuery = s;
                break;

            case SINGLE_FAVORITE:
                vTableName = FavoriteTableHelper.TABLE_NAME;
                vQuery = FavoriteTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;
            case ADD_GENERE_FOR_FILM:
                vTableName = GenreMovieTableHelper.TABLE_NAME;
                vQuery = GenreMovieTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;
        }
        int vDeleteRows = vDb.delete(vTableName, vQuery, strings);
        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }
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
            case ALL_FAVORITE:
                vTableName = FavoriteTableHelper.TABLE_NAME;
                vQuery = s;
                break;
            case SINGLE_FAVORITE:
                vTableName = FavoriteTableHelper.TABLE_NAME;
                vQuery = FavoriteTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;
            case ADD_GENERE_FOR_FILM:
                vTableName = GenreMovieTableHelper.TABLE_NAME;
                vQuery = GenreMovieTableHelper._ID + " = " + uri.getLastPathSegment();
                if (s != null) {
                    vQuery += " AND " + s;
                }
                break;

        }
        int vUpdateRows = vDb.update(vTableName, contentValues, vQuery, strings);
        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }
        return vUpdateRows;
    }
}
