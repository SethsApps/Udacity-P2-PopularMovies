package me.sethallen.popularmovies.task;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.data.FavoriteMovieContract;
import me.sethallen.popularmovies.data.FavoriteMovieDbHelper;
import me.sethallen.popularmovies.interfaces.IDisplayItems;
import me.sethallen.popularmovies.model.Movie;

public class FavoriteMovieLoaderTask extends AsyncTask<Void, Void, List<Movie>> {

    private static String                LOG_TAG         = FavoriteMovieLoaderTask.class.getSimpleName();
    private        Fragment              mCaller;
    private        IDisplayItems         mMovieDisplayer;
    private        FavoriteMovieDbHelper _dbHelper;

    public FavoriteMovieLoaderTask(Fragment caller, IDisplayItems movieDisplayer) {
        mCaller         = caller;
        mMovieDisplayer = movieDisplayer;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _dbHelper = new FavoriteMovieDbHelper(mCaller.getContext());
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {

        Cursor movieCursor = mCaller.getActivity().getContentResolver().query(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMNS,
                null,
                null,
                null);

        List<Movie> favoriteMovieList = new ArrayList<>();

        if (movieCursor.moveToFirst())
        {
            Movie movie;
            do
            {
                movie = new Movie(
                        movieCursor.getInt(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_MOVIE_ID),
                        movieCursor.getString(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_TITLE),
                        movieCursor.getString(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_OVERVIEW),
                        movieCursor.getString(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_RELEASE_DATE),
                        movieCursor.getFloat(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_VOTE_AVERAGE),
                        movieCursor.getString(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_BACKDROP_PATH),
                        movieCursor.getString(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_POSTER_PATH)
                );
                movie.SetIsFavorite(true);
                favoriteMovieList.add(movie);
            } while (movieCursor.moveToNext());
        }

        movieCursor.close();

        return favoriteMovieList;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);

        mMovieDisplayer.appendItems(movies);
    }
}