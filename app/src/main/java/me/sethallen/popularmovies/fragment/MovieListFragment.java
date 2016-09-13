package me.sethallen.popularmovies.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sethallen.popularmovies.activity.BaseActivity;
import me.sethallen.popularmovies.activity.MainActivity;
import me.sethallen.popularmovies.adapter.MovieAdapter;
import me.sethallen.popularmovies.interfaces.IDisplayItems;
import me.sethallen.popularmovies.interfaces.IFavoriteStatusObserver;
import me.sethallen.popularmovies.model.Configuration;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.service.TheMovieDBService;
import me.sethallen.popularmovies.task.FavoriteMovieLoaderTask;
import me.sethallen.popularmovies.task.TMDBMovieLoaderTask;
import me.sethallen.popularmovies.utility.NetworkHelper;
import me.sethallen.popularmovies.utility.RetrofitHelper;

public class MovieListFragment extends Fragment
        implements IDisplayItems<Movie>,
                   IFavoriteStatusObserver {

    private static String       LOG_TAG                         = MovieListFragment.class.getSimpleName();
    private static final String STATE_PAGE                      = "state-page";
    private static final String STATE_QUERY_TYPE                = "state-query-type";
    private static final String STATE_MOVIE_LIST                = "state-movie-list";
    private static String       TMDB_MOVIE_POPULAR_QUERY_TYPE   = "popular";
    private static String       TMDB_MOVIE_TOP_RATED_QUERY_TYPE = "top_rated";
    private static String       TMDB_MOVIE_FAVORITE_QUERY_TYPE  = "favorite";
    private static final int    SORT_OPTION_INDEX_MOST_POPULAR  = 0;
    private static final int    SORT_OPTION_INDEX_HIGHEST_RATED = 1;
    private static final int    SORT_OPTION_INDEX_FAVORITES     = 2;
    private static String[]     mSortDialogItems;

    private boolean                 mDualPane;
    private int                     mFragmentWidthInPixels;
    private int                     mPage;
    private String                  mQueryType;
    private OnMovieSelectedListener mListener;
    private BaseActivity            mBaseActivity;
    private GridLayoutManager       mGridLayoutManager;
    private MovieAdapter            mMovieAdapter;
    private ActionBar               mActionBar;
    private Toolbar                 mToolbar;

    @BindView(R.id.movies_recycler_view) RecyclerView mMovieRecyclerView;

    public MovieListFragment()
    {
        mSortDialogItems = new String[3];
        mSortDialogItems[SORT_OPTION_INDEX_MOST_POPULAR]  = "Most Popular";
        mSortDialogItems[SORT_OPTION_INDEX_HIGHEST_RATED] = "Highest Rated";
        mSortDialogItems[SORT_OPTION_INDEX_FAVORITES]     = "Favorites";
    }

    /**
     * Use this factory method to create a new instance of this fragment.
     * @return A new instance of fragment MovieListFragment.
     */
    public static MovieListFragment newInstance() {
        return new MovieListFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle args = new Bundle();

        args.putInt(STATE_PAGE,                mPage);
        args.putString(STATE_QUERY_TYPE,       mQueryType);
        args.putSerializable(STATE_MOVIE_LIST, mMovieAdapter.getMovieList());

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
        final View view;

        view = inflater.inflate(R.layout.fragment_main_single_pane, container, false);

        ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        mToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        mActionBar = activity.getSupportActionBar();

        view.post(new Runnable() {
            @Override
            public void run() {
                mFragmentWidthInPixels = view.getMeasuredWidth();
            }
        });

        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDualPane = ((MainActivity) getActivity()).isDualPane();

        if (mActionBar != null)
        {
            if (mDualPane) {
                mActionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                mActionBar.setHomeButtonEnabled(false);      // Disable the button
                mActionBar.setDisplayHomeAsUpEnabled(false); // Remove the left caret
                mActionBar.setDisplayShowHomeEnabled(false); // Remove the icon
            }
        }

        ArrayList<Movie> movieList = null;
        if (savedInstanceState != null) {
            mPage      = savedInstanceState.getInt(STATE_PAGE);
            mQueryType = savedInstanceState.getString(STATE_QUERY_TYPE);
            movieList  = (ArrayList<Movie>) savedInstanceState.getSerializable(STATE_MOVIE_LIST);
        } else {
            mPage      = 0;
            mQueryType = TMDB_MOVIE_POPULAR_QUERY_TYPE;
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

        if (mQueryType.equals(TMDB_MOVIE_POPULAR_QUERY_TYPE))
        {
            checkedItem = SORT_OPTION_INDEX_MOST_POPULAR;
        }
        else if (mQueryType.equals(TMDB_MOVIE_TOP_RATED_QUERY_TYPE))
        {
            checkedItem = SORT_OPTION_INDEX_HIGHEST_RATED;
        }
        else if (mQueryType.equals(TMDB_MOVIE_FAVORITE_QUERY_TYPE))
        {
            checkedItem = SORT_OPTION_INDEX_FAVORITES;
        }
        else
        {
            checkedItem = SORT_OPTION_INDEX_MOST_POPULAR;
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
                                refreshGrid(which);

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

    @Override
    public void favoriteStatusChanged(Integer movieId, boolean statusChanged) {
        Log.d(LOG_TAG, "handling favoriteStatusChanged=" + statusChanged);
        // Only reload the movies grid if statusChanged and Favorites are already what are shown.
        if (statusChanged)
        {
            if (mQueryType.equals(TMDB_MOVIE_FAVORITE_QUERY_TYPE)) {
                Log.d(LOG_TAG, "calling loadMovies due to favoriteStatusChanged");
                loadMovies();
            } else {
                for (Movie m : mMovieAdapter.getMovieList()) {
                    if (m.getId() == movieId) {
                        m.SetIsFavorite(!m.GetIsFavorite());
                        break;
                    }
                }
            }
        }
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
            appendItems(movieList);
        }
    }

    private void refreshGrid(int sortOption)
    {
        mPage = 0;
        switch (sortOption)
        {
            case SORT_OPTION_INDEX_MOST_POPULAR:
                mQueryType = TMDB_MOVIE_POPULAR_QUERY_TYPE;
                break;
            case SORT_OPTION_INDEX_HIGHEST_RATED:
                mQueryType = TMDB_MOVIE_TOP_RATED_QUERY_TYPE;
                break;
            case SORT_OPTION_INDEX_FAVORITES:
            default:
                mQueryType = TMDB_MOVIE_FAVORITE_QUERY_TYPE;
                break;

        }
        loadMovies();
    }

    private void loadMovies() {
        mPage = 1;

        if (mQueryType.equals(TMDB_MOVIE_FAVORITE_QUERY_TYPE))
        {
            FavoriteMovieLoaderTask movieLoaderTask = new FavoriteMovieLoaderTask(this, this);
            movieLoaderTask.execute();
            return;
        }

        int retryCount = 0;
        do {
            try {
                TMDBMovieLoaderTask                 movieLoaderTask = new TMDBMovieLoaderTask(this, this);
                TMDBMovieLoaderTask.MovieLoaderArgs args = movieLoaderTask.new MovieLoaderArgs(mQueryType, mPage);

                movieLoaderTask.execute(args);
                break;
            }
            catch (Exception ex)
            {
                retryCount++;
            }
        } while (retryCount < 3);
    }

    public void appendItems(List<Movie> movies)
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

            Configuration configuration = null;
            // Only attempt to load configuration if we have internet access.
            if (NetworkHelper.hasInternetAccess(getContext(), LOG_TAG)) {

                final TheMovieDBService movieApi = TheMovieDBService.Factory.create(getString(R.string.base_url_tmdb));
                String apiKey = getString(R.string.api_key_tmdb);
                Log.d(LOG_TAG, "attempting to call getConfiguration with key " + apiKey);

                configuration = RetrofitHelper.ExecuteCall(movieApi.getConfiguration(apiKey));

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
            }

            setURIs(configuration, movieList);

            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);

            addMoviesToGrid(movieList);
        }
    }

    private void addMoviesToGrid(List<Movie> movieList)
    {
        if (movieList != null) {
            if (mPage == 1) {
                mMovieAdapter.clear();
            }
            mMovieAdapter.appendItems(movieList);
        } else {
            mMovieAdapter.clear();
            String errorInfo = "Unable to load movies.";
            Log.e(LOG_TAG, errorInfo);

            Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void setURIs(Configuration configuration, List<Movie> movieList)
    {
        // Set the ImageBaseUri, PosterSize, and BackdropSize for each movie
        Uri    baseUri      = getBaseUri(configuration);
        String posterSize   = getIdealPosterSize(configuration);
        String backdropSize = getIdealBackdropSize(configuration);
        for (Movie movie : movieList) {
            movie.setImageBaseUri(baseUri);
            movie.setPosterSize(posterSize);
            Log.d(LOG_TAG, "Movie Poster Size: " + movie.getPosterSize());
            Log.d(LOG_TAG, "Movie Poster URI: " + movie.getPosterUriString());
            movie.setBackdropSize(backdropSize);
            Log.d(LOG_TAG, "Movie Backdrop Size: " + movie.getBackdropSize());
            Log.d(LOG_TAG, "Movie Backdrop URI: " + movie.getBackdropUriString());
        }
    }

    private Uri getBaseUri(Configuration tmdbConfig)
    {
        if (tmdbConfig == null) return Uri.EMPTY;
        return Uri.parse(tmdbConfig.getImages().getBaseUrl());
    }

    private int getIdealGridColumnCount()
    {
        double preferredPosterWidth = .75;
        if (mDualPane)
        {
            // Get the max posters that will fit across width of the fragment
            // and set the column count (Span Count) of grid to that amount of posters.
            int    maxPostersForScreenWidth = (int)Math.floor(mFragmentWidthInPixels / preferredPosterWidth);
            return Math.max(maxPostersForScreenWidth, 1);
        }
        else
        {
            // Get the max posters that will fit across width of screen
            // and set the column count (Span Count) of grid to that amount of posters
            double screenWidthInInches      = mBaseActivity.getScreenWidthInInches();
            int    maxPostersForScreenWidth = (int)Math.floor(screenWidthInInches / preferredPosterWidth);
            return maxPostersForScreenWidth;
        }
    }

    private String getIdealPosterSize(Configuration tmdbConfig)
    {
        if (tmdbConfig == null) return "";

        // Get the ideal poster size given the screen width and density,
        // the amount of columns in the grid, and the available poster sizes from TMBd.
        double screenWidthInInches      = mBaseActivity.getScreenWidthInInches();
        double postersPerInch           = screenWidthInInches / mGridLayoutManager.getSpanCount();
        int    posterSizePixels         = Math.round(Math.round(postersPerInch * mBaseActivity.getDisplayXDPI()));

        return tmdbConfig.getImages().getClosestPosterSize(posterSizePixels);
    }

    private String getIdealBackdropSize(Configuration tmdbConfig)
    {
        if (tmdbConfig == null) return "";
        // Get the ideal backdrop size given the screen width and density,
        // and the available backdrop sizes from TMBd.
        int screenWidthInPixels = Math.round(mBaseActivity.getDisplayWidthPixels());

        return tmdbConfig.getImages().getClosestBackdropSize(screenWidthInPixels);
    }

    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
    }
}