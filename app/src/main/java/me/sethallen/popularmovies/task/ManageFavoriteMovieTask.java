package me.sethallen.popularmovies.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import me.sethallen.popularmovies.data.FavoriteMovieContract;
import me.sethallen.popularmovies.data.FavoriteMovieDbHelper;
import me.sethallen.popularmovies.interfaces.IAsyncTaskResultHandler;
import me.sethallen.popularmovies.model.Movie;

public class ManageFavoriteMovieTask extends AsyncTask<Movie, Void, String>
{
    private static String LOG_TAG = ManageFavoriteMovieTask.class.getSimpleName();

    Context                 _context;
    IAsyncTaskResultHandler _resultHandler;
    FavoriteMovieDbHelper   _dbHelper;

    public static final String RESULT_FAILURE                      = "failure";
    public static final String RESULT_ADDED_MOVIE_TO_FAVORITES     = "added to favorites";
    public static final String RESULT_REMOVED_MOVIE_FROM_FAVORITES = "removed from favorites";

    public ManageFavoriteMovieTask(Context context, IAsyncTaskResultHandler resultHandler) {
        _context       = context;
        _resultHandler = resultHandler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _dbHelper = new FavoriteMovieDbHelper(_context);
    }

    @Override
    protected String doInBackground(Movie... params)
    {
        Movie  movie          = params[0];
        Cursor favMovieCursor = null;

        try
        {
            //Check if the movie with this movie_id  exists in the db
            Log.d(LOG_TAG, "Attempt to Open Cursor for Content URI: "
                    + FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI);

            favMovieCursor = _context.getContentResolver().query(
                    FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                    new String[]{FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID},
                    FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(movie.getId())},
                    null);

            // If it exists, delete the movie with that movie id
            if (favMovieCursor.moveToFirst())
            {
                int rowDeleted = _context.getContentResolver().delete(
                        FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                        FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movie.getId())});

                if (rowDeleted > 0)
                {
                    return RESULT_REMOVED_MOVIE_FROM_FAVORITES;
                }
                else
                {
                    return RESULT_FAILURE;
                }
            }
            else
            {
                // Otherwise, insert it using the content resolver and the base URI
                ContentValues values = new ContentValues();

                //Then add the data, along with the corresponding name of the data type,
                //so the content provider knows what kind of value is being inserted.
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movie.getId());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE, movie.getTitle());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

                // Finally, insert movie data into the database.
                Uri insertedUri = _context.getContentResolver().insert(
                        FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                        values);

                // The resulting URI contains the ID for the row.  Extract the movie rowId from the Uri.
                long movieRowId = ContentUris.parseId(insertedUri);

                if (movieRowId > 0)
                {
                    return movie.getTitle() + " " + RESULT_ADDED_MOVIE_TO_FAVORITES;
                }
                else
                {
                    return movie.getTitle() + " " + RESULT_FAILURE;
                }
            }
        }
        finally
        {
            if (favMovieCursor != null && !favMovieCursor.isClosed())
            {
                favMovieCursor.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        if (result.equals(RESULT_FAILURE))
        {
            _resultHandler.onFailure();
        }
        else
        {
            _resultHandler.onSuccess(result);
        }
    }
}
