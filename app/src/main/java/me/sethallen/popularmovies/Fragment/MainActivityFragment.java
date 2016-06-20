package me.sethallen.popularmovies.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.activity.BaseActivity;
import me.sethallen.popularmovies.adapter.MovieAdapter;
import me.sethallen.popularmovies.model.Configuration;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.model.MovieResponse;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.service.TheMovieDBService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityFragment.OnMovieSelectedListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityFragment extends Fragment {

    private static String       LOG_TAG                         = MainActivityFragment.class.getSimpleName();
    private static final String STATE_PAGE                      = "state-page";
    private static final String STATE_QUERY_TYPE                = "state-query-type";
    private static final String STATE_MOVIE_LIST                = "state-movie-list";
    private static String       TMDB_MOVIE_POPULAR_QUERY_TYPE   = "popular";
    private static String       TMDB_MOVIE_TOP_RATED_QUERY_TYPE = "top_rated";
    private static String[]     mSortDialogItems                = new String[] {"Most Popular", "Highest Rated"};

    private int                     mPage;
    private String                  mQueryType;
    private OnMovieSelectedListener mListener;
    private BaseActivity            mBaseActivity;
    private GridLayoutManager       mGridLayoutManager;
    private RecyclerView            mMovieRecyclerView;
    private MovieAdapter            mMovieAdapter;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment.
     * @return A new instance of fragment MainActivityFragment.
     */
    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle args = new Bundle();

        args.putInt(STATE_PAGE,                mPage);
        args.putString(STATE_QUERY_TYPE,       mQueryType);
        args.putSerializable(STATE_MOVIE_LIST, mMovieAdapter.getMovieList());
        // TODO: Save scroll position after adding Endless Scrolling in P2

        outState.putAll(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (view instanceof RecyclerView) {
            mMovieRecyclerView = (RecyclerView)view;
        }

        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Movie> movieList = null;
        if (savedInstanceState != null) {
            mPage        = savedInstanceState.getInt(STATE_PAGE);
            mQueryType   = savedInstanceState.getString(STATE_QUERY_TYPE);
            movieList    = (ArrayList<Movie>) savedInstanceState.getSerializable(STATE_MOVIE_LIST);
            // TODO: Add tracking of scroll position and scrolling back to that position, after adding Endless Scrolling in P2
        } else {
            mPage        = 0;
            mQueryType   = TMDB_MOVIE_POPULAR_QUERY_TYPE;
        }

        intializeGrid(movieList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                openSortDialog();
                return true;
            default:
                break;
        }

        return false;
    }

    private void openSortDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final int checkedItem;
        if (mQueryType.equals(TMDB_MOVIE_POPULAR_QUERY_TYPE)){
            checkedItem = 0;
        }
        else {
            checkedItem = 1;
        }

        AlertDialog dialog;

        builder.setTitle(getString(R.string.sort_dialog_title))
                .setSingleChoiceItems(
                        mSortDialogItems,
                        checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // If the user selected the option that was previously checked
                                // no sorting and reloading of the movies is needed.
                                if (which == checkedItem)
                                {
                                    return;
                                }
                                mPage = 0;
                                if (which == 0)
                                {
                                    mQueryType = TMDB_MOVIE_POPULAR_QUERY_TYPE;
                                }
                                else
                                {
                                    mQueryType = TMDB_MOVIE_TOP_RATED_QUERY_TYPE;
                                }
                                loadMovies();

                                dialog.dismiss();
                            }
                        }
                );

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieSelectedListener) {
            mListener = (OnMovieSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieSelectedListener");
        }

        if (context instanceof BaseActivity)
        {
            mBaseActivity = (BaseActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must extend BaseActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
    }

    private void intializeGrid(ArrayList<Movie> movieList)
    {
        mGridLayoutManager = new GridLayoutManager(getContext(), getIdealGridColumnCount());
        mMovieRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieAdapter = new MovieAdapter(mListener);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        if (movieList == null) {
            loadMovies();
        } else {
            appendMoviesToGrid(movieList);
        }
    }

    public void loadMovies() {
        mPage += 1;

        int retryCount = 0;
        do {
            try {
                MovieLoaderTask movieLoaderTask = new MovieLoaderTask();
                MovieLoaderArgs args            = new MovieLoaderArgs(mQueryType, mPage);

                movieLoaderTask.execute(args);
                break;
            }
            catch (Exception ex)
            {
                retryCount++;
            }
        } while (retryCount < 3);
    }

    private class MovieLoaderArgs
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

    private class MovieLoaderTask extends AsyncTask<MovieLoaderArgs, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(MovieLoaderArgs... params) {

            MovieLoaderArgs args = params[0];
            String apiKey = getString(R.string.api_key_tmdb);

            final TheMovieDBService movieApi = TheMovieDBService.Factory.create(getString(R.string.base_url_tmdb));

            Log.d(LOG_TAG, "attempting to call getMovies with key " + apiKey
                    + " and QueryType " + args.getQueryType()
                    + " for Page " + args.getPage());

            MovieResponse movieResponse = executeCall(movieApi.getMovies(args.getQueryType(), apiKey, args.getPage()));

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

            return movieResultList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);

            appendMoviesToGrid(movies);
        }
    }

    private void appendMoviesToGrid(List<Movie> movies)
    {
        UpdateMoviesWithIdealSizeAndAddToGridTask task = new UpdateMoviesWithIdealSizeAndAddToGridTask();
        task.execute(movies);
    }

    private class UpdateMoviesWithIdealSizeAndAddToGridTask extends AsyncTask<List<Movie>, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(List<Movie>... params) {
            if (params == null || params.length == 0 || params[0] == null) {
                return null;
            }
            List<Movie> movieList = params[0];

            final TheMovieDBService movieApi = TheMovieDBService.Factory.create(getString(R.string.base_url_tmdb));
            String apiKey = getString(R.string.api_key_tmdb);
            Log.d(LOG_TAG, "attempting to call getConfiguration with key " + apiKey);

            Configuration configuration = executeCall(movieApi.getConfiguration(apiKey));

            if (configuration == null) {
                Log.d(LOG_TAG, "response from getConfiguration() call is null");
                return null;
            }

            for (String size : configuration.getImages().getPosterSizes()) {
                Log.d(LOG_TAG, "Poster Size: " + size);
            }

            for (String size : configuration.getImages().getBackdropSizes()) {
                Log.d(LOG_TAG, "Backdrop Size: " + size);
            }

            for (String size : configuration.getImages().getLogoSizes()) {
                Log.d(LOG_TAG, "Logo Size: " + size);
            }

            for (String size : configuration.getImages().getProfileSizes()) {
                Log.d(LOG_TAG, "Profile Size: " + size);
            }

            for (String size : configuration.getImages().getStillSizes()) {
                Log.d(LOG_TAG, "Still Size: " + size);
            }

            setURIs(configuration, movieList);

            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);

            if (movieList != null) {
                if (mPage == 1) {
                    mMovieAdapter.clear();
                }
                mMovieAdapter.appendItems(movieList);
            } else {
                String errorInfo = "Unable to load movies.";
                Log.e(LOG_TAG, errorInfo);

                Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void setURIs(Configuration configuration, List<Movie> movieList)//, String apiKey)
    {
        // Set the ImageBaseUri, PosterSize, and BackdropSize for each movie
        Uri    baseUri      = Uri.parse(configuration.getImages().getBaseUrl());
        String posterSize   = getIdealPosterSize(configuration);
        String backdropSize = getIdealBackdropSize(configuration);
        for (Movie movie : movieList) {
            movie.setImageBaseUri(baseUri);
            movie.setPosterSize(posterSize);
            Log.d(LOG_TAG, "Movie Poster Size: " + movie.getPosterSize());
            Log.d(LOG_TAG, "Movie Poster URI: " + movie.getPosterUri().toString());
            movie.setBackdropSize(backdropSize);
            Log.d(LOG_TAG, "Movie Backdrop Size: " + movie.getBackdropSize());
            Log.d(LOG_TAG, "Movie Backdrop URI: " + movie.getBackdropUri().toString());
        }
    }

    private int getIdealGridColumnCount()
    {
        // Get the max posters that will fit across width of screen
        // and set the column count (Span Count) of grid that amount of posters
        double screenWidthInInches      = mBaseActivity.getScreenWidthInInches();
        double preferredPosterWidth     = .75;
        int    maxPostersForScreenWidth = (int)Math.floor(screenWidthInInches / preferredPosterWidth);
        return maxPostersForScreenWidth;
    }

    private String getIdealPosterSize(Configuration tmdbConfig)
    {
        // Get the ideal poster size given the screen width and density,
        // the amount of columns in the grid, and the available poster sizes from TMBd.
        double screenWidthInInches      = mBaseActivity.getScreenWidthInInches();
        double postersPerInch           = screenWidthInInches / mGridLayoutManager.getSpanCount();
        int    posterSizePixels         = Math.round(Math.round(postersPerInch * mBaseActivity.getDisplayXDPI()));

        return tmdbConfig.getImages().getClosestPosterSize(posterSizePixels);
    }

    private String getIdealBackdropSize(Configuration tmdbConfig)
    {
        // Get the ideal backdrop size given the screen width and density,
        // and the available backdrop sizes from TMBd.
        int screenWidthInPixels = Math.round(mBaseActivity.getDisplayWidthPixels());

        return tmdbConfig.getImages().getClosestBackdropSize(screenWidthInPixels);
    }

    private <T> T executeCall(Call<T> apiCall)
    {
        Response<T> apiResponse;
        try {
            apiResponse = apiCall.execute();
        }
        catch (IOException ioEx) {
            Log.e(LOG_TAG, "IOException attempting to execute api call", ioEx);
            return null;
        }

        if (!apiResponse.isSuccessful()) {
            Log.d(LOG_TAG, "api call was unsuccessful");
            return null;
        }

        T response = apiResponse.body();

        if (response == null) {
            Log.d(LOG_TAG, "response from api call is null");
            return null;
        }

        return response;
    }
}