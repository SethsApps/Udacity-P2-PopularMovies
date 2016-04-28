package me.sethallen.popularmovies.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import java.util.List;

import me.sethallen.popularmovies.Adapter.MovieAdapter;
import me.sethallen.popularmovies.Model.Configuration;
import me.sethallen.popularmovies.Model.Movie;
import me.sethallen.popularmovies.Model.MovieResponse;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.Service.TheMovieDBService;
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

    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT            = "column-count";
    private static String TMDB_MOVIE_POPULAR_QUERY_TYPE     = "popular";
    private static String TMDB_MOVIE_TOP_RATED_QUERY_TYPE   = "top_rated";

    private int                     mColumnCount = 3;
    private int                     mPage        = 0;
    private String mQueryType = TMDB_MOVIE_POPULAR_QUERY_TYPE;
    private OnMovieSelectedListener mListener;
    private MovieAdapter            mMovieAdapter;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param columnCount Column Count indicating how many columns to show in the grid move grid.
     * @return A new instance of fragment MainActivityFragment.
     */
    public static MainActivityFragment newInstance(int columnCount) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
                mPage = 0;
                if (mQueryType.equals(TMDB_MOVIE_POPULAR_QUERY_TYPE)){
                    mQueryType = TMDB_MOVIE_TOP_RATED_QUERY_TYPE;
                }
                else {
                    mQueryType = TMDB_MOVIE_POPULAR_QUERY_TYPE;
                }
                loadMovies();
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (view instanceof RecyclerView) {
            Context context                = view.getContext();
            RecyclerView movieRecyclerView = (RecyclerView)view;

            // TODO: Get screen layout width to determine amount of columns to show

            if (mColumnCount <= 1) {
                movieRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else {
                movieRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                movieRecyclerView.setHasFixedSize(true);
            }

            mMovieAdapter = new MovieAdapter(mListener);

            loadMovies();

            movieRecyclerView.setAdapter(mMovieAdapter);
        }

        return view;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
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
            String apiKey        = getString(R.string.api_key_tmdb);

            final TheMovieDBService movieApi = TheMovieDBService.Factory.create(getString(R.string.base_url_tmdb));

            Log.d(LOG_TAG, "attempting to call getMovies with key " + apiKey
                    + " and QueryType " + args.getQueryType()
                    + " for Page " + args.getPage());

//            MovieResponse movieResponse
//                    = executeCall(movieApi.getMovies(apiKey, args.getSortBy(), args.getPage()));

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

            Configuration configResponse = executeCall(movieApi.getConfiguration(apiKey));
            if (configResponse == null) {
                Log.d(LOG_TAG, "response from getConfiguration() call is null");
                return null;
            }

            setURIs(configResponse, movieResultList, apiKey);

            return movieResultList;
        }

        private void setURIs(Configuration configuration, List<Movie> movieList, String apiKey)
        {
            // Set the PosterUrl for each movie
            //String baseUrl      = configuration.getImages().getBaseUrl();
            Uri    baseUri      = Uri.parse(configuration.getImages().getBaseUrl());
            String posterSize   = configuration.getImages().getPosterSizes().get(0);
            String backdropSize = configuration.getImages().getBackdropSizes().get(0);
            for (Movie movie : movieList) {
                movie.setPosterUri(getUri(baseUri, posterSize, movie.getPosterPath(), apiKey));
                movie.setBackdropUri(getUri(baseUri, backdropSize, movie.getBackdropPath(), apiKey));
            }
        }

        private Uri getUri(Uri baseUri, String size, String uniquePath, String apiKey)
        {
            Uri newURi = baseUri.buildUpon()
                    .appendEncodedPath(size + uniquePath)
                    .build();

            Log.d(LOG_TAG, "URI was: " + newURi.toString());
            return newURi;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);

            if (movies != null) {
                if (mPage == 1) {
                    mMovieAdapter.clear();
                }
                mMovieAdapter.appendItems(movies);
            } else {
                String errorInfo = "No Success: ";// + response.errorBody().toString();

                Log.d(LOG_TAG, errorInfo);

                Toast.makeText(getActivity(),
                        errorInfo, Toast.LENGTH_LONG)
                        .show();
            }
        }

        public <T> T executeCall(Call<T> apiCall)
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
}