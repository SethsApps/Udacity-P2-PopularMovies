package me.sethallen.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.fragment.MainActivityFragment;
import me.sethallen.popularmovies.model.Movie;

public class MainActivity extends BaseActivity
        implements MainActivityFragment.OnMovieSelectedListener {

    public final static String MOVIE_ARG = "me.sethallen.popularmovies.MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** TODO: Add settings later if applicable
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: Open Settings menu
                return true;
            default:
                break;
        }

        return false;
    }
     */

    @Override
    public void onMovieSelected(Movie movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(MOVIE_ARG, movie);
        startActivity(movieDetailIntent);
    }
}