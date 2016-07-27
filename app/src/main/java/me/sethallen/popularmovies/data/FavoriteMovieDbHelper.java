package me.sethallen.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.sethallen.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper
{
    private static final String LOG_TAG          = FavoriteMovieDbHelper.class.getSimpleName();
    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = FavoriteMovieEntry.TABLE_NAME + ".db";

    private static final String SQL_CREATE_TABLE_FAVORITE_MOVIE =
            "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME
            + "("
                    + FavoriteMovieEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FavoriteMovieEntry.COLUMN_MOVIE_ID      + " TEXT NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_TITLE         + " TEXT NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_OVERVIEW      + " TEXT NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_VOTE_AVERAGE  + " REAL NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                    + FavoriteMovieEntry.COLUMN_POSTER_PATH   + " TEXT NOT NULL, "
                    + " UNIQUE "
                        + "("
                            + FavoriteMovieEntry.COLUMN_MOVIE_ID
                        + ") ON CONFLICT REPLACE"
            + ");";

    private static final String SQL_DROP_TABLE_FAVORITE_MOVIE =
            "DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME;

    public FavoriteMovieDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Creating " + DATABASE_NAME + " database");
        db.execSQL(SQL_CREATE_TABLE_FAVORITE_MOVIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "Upgrading " + DATABASE_NAME
                + " from version " + oldVersion
                + " to version " + newVersion);

        db.execSQL(SQL_DROP_TABLE_FAVORITE_MOVIE);
        onCreate(db);
    }
}