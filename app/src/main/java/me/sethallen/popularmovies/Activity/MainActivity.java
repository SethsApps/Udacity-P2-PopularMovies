package me.sethallen.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.fragment.MainActivityFragment;
import me.sethallen.popularmovies.interfaces.IFavoriteStatusObserver;
import me.sethallen.popularmovies.model.Movie;

public class MainActivity extends BaseActivity
        implements MainActivityFragment.OnMovieSelectedListener {

    private      static String LOG_TAG                 = MainActivity.class.getSimpleName();
    public final static String MOVIE_ARG               = "me.sethallen.popularmovies.MOVIE";
    public final static String FAVORITE_STATUS_CHANGED = "fav-status-changed";
    public final static int    REQUEST_CODE            = 22;

    private IFavoriteStatusObserver mMovieGridFragment;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (REQUEST_CODE) : {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    boolean favoriteStatusChanged = data.getBooleanExtra(FAVORITE_STATUS_CHANGED, false);
                    Log.d(LOG_TAG, "setting movieGrid to favoriteStatusChanged=" + favoriteStatusChanged);
                    // Refreshes the movie grid if it's in "Favorites" view
                    mMovieGridFragment = (IFavoriteStatusObserver)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    mMovieGridFragment.favoriteStatusChanged(favoriteStatusChanged);
                }
                break;
            }
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(MOVIE_ARG, movie);
        startActivityForResult(movieDetailIntent, REQUEST_CODE);
    }
}