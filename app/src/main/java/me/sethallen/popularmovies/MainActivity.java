package me.sethallen.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
        // TODO: Launch new intent to show details of the selected movie
    }
}