package me.sethallen.popularmovies.activity;

import android.os.Bundle;

import me.sethallen.popularmovies.fragment.MovieDetailFragment;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.R;

public class MovieDetailActivity extends BaseActivity {

    private static String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        /**
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }
         */

        if (savedInstanceState != null) {
            return;
        }

        if (findViewById(R.id.fragment_movie_detail_container) != null) {
            // During initial setup, plug in the details fragment.
            Movie               movie               = getIntent().getParcelableExtra(MainActivity.MOVIE_ARG);
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_movie_detail_container, movieDetailFragment)
                    .commit();
        }
    }
}
