package me.sethallen.popularmovies.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sethallen.popularmovies.fragment.MovieDetailFragment;
import me.sethallen.popularmovies.interfaces.IFavoriteStatusObserver;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.model.Review;
import me.sethallen.popularmovies.model.Video;
import me.sethallen.popularmovies.utility.UriHelper;

public class MovieDetailActivity extends BaseActivity
        implements MovieDetailFragment.OnVideoSelectedListener,
                   MovieDetailFragment.OnReviewSelectedListener,
                   IFavoriteStatusObserver {

    private static String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private boolean                 mFavoriteStatusChanged = false;

    @BindView(R.id.fragment_movie_detail_container) FrameLayout mMovieDetailFragmentContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
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

        if (mMovieDetailFragmentContainer != null) {
            // During initial setup, plug in the details fragment, replacing the mMovieDetailFragmentContainer
            Movie               movie               = getIntent().getParcelableExtra(MainActivity.MOVIE_ARG);
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mMovieDetailFragmentContainer.getId(), movieDetailFragment)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause");
        setResultAndFinish();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Log.d(LOG_TAG, "onKeyDown => KEYCODE_BACK");
            setResultAndFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(LOG_TAG, "OnBackPressed");
        setResultAndFinish();
    }

    private void setResultAndFinish()
    {
        Intent resultIntent = new Intent();
        Log.d(LOG_TAG, "Setting favorite status changed to: " + mFavoriteStatusChanged);
        resultIntent.putExtra(MainActivity.FAVORITE_STATUS_CHANGED, mFavoriteStatusChanged);
        setResult(AppCompatActivity.RESULT_OK, resultIntent);
        finish();
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
    public void favoriteStatusChanged(boolean statusChanged) {
        Log.d(LOG_TAG, "changed status changing from: " + mFavoriteStatusChanged + " to " + statusChanged);
        mFavoriteStatusChanged = statusChanged;
    }
}