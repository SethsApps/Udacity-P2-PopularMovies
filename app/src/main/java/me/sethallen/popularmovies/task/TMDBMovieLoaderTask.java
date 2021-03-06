package me.sethallen.popularmovies.task;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.data.FavoriteMovieContract;
import me.sethallen.popularmovies.interfaces.IDisplayItems;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.model.Movies;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.service.TheMovieDBService;
import me.sethallen.popularmovies.utility.RetrofitHelper;

public class TMDBMovieLoaderTask extends AsyncTask<TMDBMovieLoaderTask.MovieLoaderArgs, Void, List<Movie>> {

    private static String        LOG_TAG         = TMDBMovieLoaderTask.class.getSimpleName();
    private Fragment             mCaller;
    private IDisplayItems<Movie> mMovieDisplayer;

    public TMDBMovieLoaderTask(Fragment caller, IDisplayItems<Movie> movieDisplayer) {
        mCaller         = caller;
        mMovieDisplayer = movieDisplayer;
    }

    @Override
    protected List<Movie> doInBackground(MovieLoaderArgs... params) {

        MovieLoaderArgs args = params[0];
        String apiKey = mCaller.getString(R.string.api_key_tmdb);

        final TheMovieDBService movieApi = TheMovieDBService.Factory.create(mCaller.getString(R.string.base_url_tmdb));

        Log.d(LOG_TAG, "attempting to call getMovies with key " + apiKey
                + " and QueryType " + args.getQueryType()
                + " for Page " + args.getPage());

        Movies movieResponse = RetrofitHelper.ExecuteCall(movieApi.getMovies(args.getQueryType(), apiKey, args.getPage()));

        if (movieResponse == null) {
            Log.d(LOG_TAG, "response from getMovies() call is null");
            return null;
        }
        List<Movie> movieResultList = movieResponse.getResults();
        if (movieResultList == null) {
            Log.d(LOG_TAG, "movie results list from getMovies() call is null");
            return null;
        } else {
            Log.d(LOG_TAG, "movie results list from getMovies() call size is: " + movieResultList.size());
        }

        movieResultList = setFavoriteStatus(movieResultList);

        return movieResultList;
    }

    private List<Movie> setFavoriteStatus(List<Movie> movies) {
        ContentResolver contentResolver = mCaller.getActivity().getContentResolver();
        Cursor favMovieCursor;
        for (Movie m : movies) {
            favMovieCursor = contentResolver.query(
                    FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                    new String[]{FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID},
                    FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " IN (?)",
                    new String[]{Integer.toString(m.getId())},
                    null);

            if (favMovieCursor == null) continue;

            if (favMovieCursor.moveToFirst()) m.SetIsFavorite(true);
        }

        return movies;
    }

    private List<Movie> setFavoriteStatusOld(List<Movie> movies) {
        StringBuilder idBuilder = new StringBuilder();
        for (Movie m : movies) {
            idBuilder.append(m.getId());
            idBuilder.append(",");
        }
        idBuilder.deleteCharAt(idBuilder.length() - 1);

        Cursor favMovieCursor = mCaller.getActivity().getContentResolver().query(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                new String[]{FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID},
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " IN (?)",
                new String[]{idBuilder.toString()},
                null);

        if (favMovieCursor.moveToFirst())
        {
            Integer favMovieId;
            do
            {
                favMovieId = favMovieCursor.getInt(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_INDEX_MOVIE_ID);
                for (Movie m : movies) {
                    if (m.getId() == favMovieId) {
                        m.SetIsFavorite(true);
                    }
                }
            } while (favMovieCursor.moveToNext());
        }

        return movies;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);

        mMovieDisplayer.appendItems(movies);
    }

    public class MovieLoaderArgs
    {
        private String mQueryType;
        private String mPage;

        public String getQueryType() {
            return mQueryType;
        }

        public String getPage() {
            return mPage;
        }

        public MovieLoaderArgs(String queryType, Integer page) {
            this.mQueryType = queryType;
            this.mPage      = String.valueOf(page);
        }
    }
}