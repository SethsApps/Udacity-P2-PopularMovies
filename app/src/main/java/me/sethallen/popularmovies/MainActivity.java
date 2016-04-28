package me.sethallen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.sethallen.popularmovies.Activity.MovieDetailActivity;
import me.sethallen.popularmovies.Fragment.MainActivityFragment;
import me.sethallen.popularmovies.Model.Movie;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityFragment.OnMovieSelectedListener {

    public final static String MOVIE_ARG = "me.sethallen.popularmovies.MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    public void onMovieSelected(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ARG, movie);
        startActivity(intent);
    }
}