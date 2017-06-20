package com.alexisalulema.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {
    public static final String AUTHORITY = "com.alexisalulema.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favorites";

        // It has an automatically produced, "_ID" column in addition to the two below
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
    }
}
