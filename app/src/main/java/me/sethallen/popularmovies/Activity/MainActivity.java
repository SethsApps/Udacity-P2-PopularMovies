package me.sethallen.popularmovies.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.fragment.MovieDetailFragment;
import me.sethallen.popularmovies.fragment.MovieListFragment;
import me.sethallen.popularmovies.interfaces.IFavoriteStatusObserver;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.model.Review;
import me.sethallen.popularmovies.model.Video;
import me.sethallen.popularmovies.utility.UriHelper;

public class MainActivity extends BaseActivity
        implements MovieListFragment.OnMovieSelectedListener,
                    MovieDetailFragment.OnVideoSelectedListener,
                    MovieDetailFragment.OnReviewSelectedListener,
                    IFavoriteStatusObserver {

    private      static String LOG_TAG                 = MainActivity.class.getSimpleName();
    public final static String MOVIE_ARG               = "me.sethallen.popularmovies.MOVIE";
    public final static String FAVORITE_MOVIE_ID       = "fav-movie-id";
    public final static String FAVORITE_STATUS_CHANGED = "fav-status-changed";
    public final static int    REQUEST_CODE            = 22;


    private static final String MASTER_FRAGMENT                 = "master-fragment";
    private static final String DETAIL_FRAGMENT                 = "detail-fragment";

    private boolean                 mDualPane;
    private String                  mLastSinglePaneFragment = null;
    private IFavoriteStatusObserver mMovieGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragment_container) == null) {
            mDualPane = true;
        } else {
            mDualPane = false;
            // Check whether the activity is using the layout version with
            // the fragment_container FrameLayout. If so, we must add the first fragment
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of MovieListFragment
            MovieListFragment firstFragment = new MovieListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, firstFragment, MASTER_FRAGMENT)
                    .commit();
        }
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
                    Integer movieId               = data.getIntExtra(FAVORITE_MOVIE_ID, -1);
                    boolean favoriteStatusChanged = data.getBooleanExtra(FAVORITE_STATUS_CHANGED, false);
                    onFavoriteStatusChanges(movieId, favoriteStatusChanged);
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (isDualPane()) {
            MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie);
            replaceFragment(detailFragment, movie);
        }
        else
        {
            Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
            movieDetailIntent.putExtra(MOVIE_ARG, movie);
            startActivityForResult(movieDetailIntent, REQUEST_CODE);
        }
    }

    private void replaceFragment (Fragment fragment, Movie movie){
        String backStateName =  fragment.getClass().getName() + movie.getTitle();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.detail_dual, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onVideoSelected(Video video) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, UriHelper.getYouTubeUri(video.getKey()));
            this.startActivity(intent);
        }
    }

    @Override
    public void onReviewSelected(Review review) {
        Uri    reviewUri = Uri.parse(review.getUrl());
        Intent intent    = new Intent(Intent.ACTION_VIEW, reviewUri);
        startActivity(intent);
    }

    @Override
    public void favoriteStatusChanged(Integer movieId, boolean statusChanged) {
        onFavoriteStatusChanges(movieId, statusChanged);
    }

    private void onFavoriteStatusChanges(Integer movieId, boolean favoriteStatusChanged)
    {
        Log.d(LOG_TAG, "setting movieGrid to favoriteStatusChanged=" + favoriteStatusChanged);
        // Refreshes the movie grid if it's in "Favorites" view
        if (isDualPane()) {
            mMovieGridFragment = (IFavoriteStatusObserver)getSupportFragmentManager().findFragmentById(R.id.master_dual);
        } else {
            mMovieGridFragment = (IFavoriteStatusObserver)getSupportFragmentManager().findFragmentByTag(MASTER_FRAGMENT);
        }
        mMovieGridFragment.favoriteStatusChanged(movieId, favoriteStatusChanged);
    }

    public boolean isDualPane()
    {
        return mDualPane;
    }
}