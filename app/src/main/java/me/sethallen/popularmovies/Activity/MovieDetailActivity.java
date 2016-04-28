package me.sethallen.popularmovies.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import me.sethallen.popularmovies.MainActivity;
import me.sethallen.popularmovies.Model.Movie;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.View.WrapContentDraweeView;

public class MovieDetailActivity extends AppCompatActivity {

    private static String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

//        TODO: Finish implementing this "Favorite" button in P2 of PopularMovies
//        FloatingActionButton fabFavorite = (FloatingActionButton) findViewById(R.id.fab_favorite);
//        fabFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Save Favorite", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(MainActivity.MOVIE_ARG);

        Log.d(LOG_TAG, "Backdrop Uri: " + movie.getBackdropUri().toString());

        Log.d(LOG_TAG, "Attempting to set Backdrop ImageView with Backdrop Path: " + movie.getBackdropPath());
        WrapContentDraweeView backdropImageView = (WrapContentDraweeView) findViewById(R.id.backdrop_image_view);
        backdropImageView.setImageURI(movie.getBackdropUri());

        Log.d(LOG_TAG, "Attempting to set Poster ImageView with Poster Path: " + movie.getPosterPath());
        WrapContentDraweeView posterImageView = (WrapContentDraweeView) findViewById(R.id.card_view_movie_poster);
        posterImageView.setImageURI(movie.getPosterUri());

        Log.d(LOG_TAG, "Attempting to set TextView with Title: " + movie.getTitle());
        TextView contentTextViewTitle = (TextView) findViewById(R.id.content_movie_detail_title);
        contentTextViewTitle.setText(movie.getTitle());

        Log.d(LOG_TAG, "Attempting to set TextView with ReleaseDate: " + movie.getReleaseDate());
        TextView contentTextViewReleaseDate = (TextView) findViewById(R.id.content_movie_detail_release_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, y");
        contentTextViewReleaseDate.setText("Released: " + sdf.format(movie.getReleaseDateAsDate()));

        Log.d(LOG_TAG, "Attempting to set TextView with VoteAverage: " + movie.getVoteAverage());
        TextView contentTextViewVoteAverage = (TextView) findViewById(R.id.content_movie_detail_average_rating);
        contentTextViewVoteAverage.setText("Rating: " + Float.toString(movie.getVoteAverage()) + "/10");

        Log.d(LOG_TAG, "Attempting to set TextView with Overview: " + movie.getOverview());
        TextView contentTextViewOverview = (TextView) findViewById(R.id.content_movie_detail_text_view);
        contentTextViewOverview.setText(movie.getOverview());
    }
}
