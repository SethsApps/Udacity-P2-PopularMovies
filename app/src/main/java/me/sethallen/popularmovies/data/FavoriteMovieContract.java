package me.sethallen.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMovieContract {

    public static final String CONTENT_AUTHORITY   = "me.sethallen.popularmovies";
    public static final Uri    BASE_CONTENT_URI    = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITE_MOVIE = "favorite_movie";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri    CONTENT_URI       = BASE_CONTENT_URI.buildUpon()
                                                                       .appendPath(PATH_FAVORITE_MOVIE)
                                                                       .build();

        public static final String CONTENT_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY
                + "/" + PATH_FAVORITE_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY
                + "/" + PATH_FAVORITE_MOVIE;

        public static final String TABLE_NAME           = "favorite_movie";

        public static final String COLUMN_MOVIE_ID      = "movie_id";
        public static final String COLUMN_TITLE         = "title";
        public static final String COLUMN_OVERVIEW      = "overview";
        public static final String COLUMN_RELEASE_DATE  = "release_date";
        public static final String COLUMN_VOTE_AVERAGE  = "vote_average";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH   = "poster_path";

        public static final int COLUMN_INDEX_MOVIE_ID      = 1;
        public static final int COLUMN_INDEX_TITLE         = 2;
        public static final int COLUMN_INDEX_OVERVIEW      = 3;
        public static final int COLUMN_INDEX_RELEASE_DATE  = 4;
        public static final int COLUMN_INDEX_VOTE_AVERAGE  = 5;
        public static final int COLUMN_INDEX_BACKDROP_PATH = 6;
        public static final int COLUMN_INDEX_POSTER_PATH   = 7;

        public static final String[] COLUMNS =
                {
                        TABLE_NAME + "." + _ID,
                        COLUMN_MOVIE_ID,
                        COLUMN_TITLE,
                        COLUMN_OVERVIEW,
                        COLUMN_RELEASE_DATE,
                        COLUMN_VOTE_AVERAGE,
                        COLUMN_BACKDROP_PATH,
                        COLUMN_POSTER_PATH

                };

        public static Uri BuildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
