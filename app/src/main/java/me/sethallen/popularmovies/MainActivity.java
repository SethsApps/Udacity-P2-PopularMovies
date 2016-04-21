package me.sethallen.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.sethallen.popularmovies.Fragment.MainActivityFragment;
import me.sethallen.popularmovies.Model.Movie;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityFragment.OnMovieSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        // TODO: Launch new intent to show details of the selected movie
    }
}