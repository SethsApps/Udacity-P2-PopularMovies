package me.sethallen.popularmovies.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int                     mColumnCount = 3;
    private int                     mPage = 0;
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
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
                MovieLoaderArgs args = new MovieLoaderArgs("popularity.desc", mPage);
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
        private String mSortBy;
        private String mPage;

        public String getSortBy() {
            return mSortBy;
        }

        public String getPage() {
            return mPage;
        }

        public MovieLoaderArgs(String sortBy, Integer page) {
            this.mSortBy = sortBy;
            this.mPage   = String.valueOf(page);
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

            MovieResponse movieResponse
                    = executeCall(movieApi.getMovies(apiKey, args.getSortBy(), args.getPage()));

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

            // Set the PosterUrl for each movie
            String baseUrl    = configResponse.getImages().getBaseUrl();
            String posterSize = configResponse.getImages().getPosterSizes().get(0);
            for (Movie movie : movieResultList) {
                try {
                    movie.setPosterUrl(new URL(
                            baseUrl
                            + posterSize + "/"
                            + movie.getPosterPath()
                            + "?api_key=" + apiKey).toString());
                } catch (MalformedURLException malformedURLEx) {
                    Log.e(LOG_TAG, "MalformedURLException attempting to create movie poster path", malformedURLEx);
                }
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

            if (movies != null) {
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