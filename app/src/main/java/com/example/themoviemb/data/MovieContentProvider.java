package com.example.themoviemb.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;

public class MovieContentProvider {








    public static final String MIME_TYPE_MOVIES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_movies";
    public static final String MIME_TYPE_MOVIE = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.single_movie";
    public static Uri MOVIES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_MOVIES);
    private MovieDatabaseHelper dBHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
}
