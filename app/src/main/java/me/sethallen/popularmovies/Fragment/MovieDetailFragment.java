package me.sethallen.popularmovies.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.view.WrapContentDraweeView;

public class MovieDetailFragment extends Fragment {

    private static       String LOG_TAG   = MovieDetailFragment.class.getSimpleName();
    private static final String ARG_MOVIE = "movie";
    private              Movie  mMovie;
    private              AppBarLayout            mAppBarLayout;
    private              CollapsingToolbarLayout mCollapsingToolbar;
    private              Toolbar                 mToolbar;

    public MovieDetailFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie Movie Parameter.
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mAppBarLayout      = (AppBarLayout) v.findViewById(R.id.app_bar);
        mCollapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        mToolbar           = (Toolbar) v.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(mToolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (getArguments() == null) {
            return v;
        }

        mMovie = getArguments().getParcelable(ARG_MOVIE);
        if (mMovie == null)
        {
            return v;
        }

        //        TODO: Finish implementing this "Favorite" button in P2 of PopularMovies
//        FloatingActionButton fabFavorite = (FloatingActionButton) findViewById(R.id.fab_favorite);
//        fabFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Save Favorite", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Log.d(LOG_TAG, "Backdrop Uri: " + mMovie.getBackdropUri().toString());

        WrapContentDraweeView imageView;
        TextView              textView;

        Log.d(LOG_TAG, "Attempting to set Backdrop ImageView with Backdrop Path: " + mMovie.getBackdropPath());
        imageView = (WrapContentDraweeView) v.findViewById(R.id.backdrop_image_view);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mMovie.getBackdropUri())
                .setPostprocessor(new BasePostprocessor() {
                    @Override
                    public String getName() {
                        return "DynamicThemeFromImagePostProcessor";
                    }

                    @Override
                    public void process(Bitmap bitmap) {
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch swatch;
                                        if ((swatch = palette.getVibrantSwatch()) == null) {
                                            if ((swatch = palette.getDarkVibrantSwatch()) == null) {
                                                if ((swatch = palette.getLightVibrantSwatch()) == null) {
                                                    if ((swatch = palette.getMutedSwatch()) == null) {
                                                        if ((swatch = palette.getDarkMutedSwatch()) == null) {
                                                            if ((swatch = palette.getLightMutedSwatch()) == null) {
                                                                // No swatches for the image were found
                                                                return;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        int mutedColor = swatch.getRgb();
                                        mCollapsingToolbar.setBackgroundColor(mutedColor);
                                        mCollapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
                                        mCollapsingToolbar.setContentScrimColor(palette.getMutedColor(mutedColor));
                                    }
                                });
                    }
                })
                .build();

        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();

        imageView.setController(controller);

        Log.d(LOG_TAG, "Attempting to set Poster ImageView with Poster Path: " + mMovie.getPosterPath());
        imageView = (WrapContentDraweeView) v.findViewById(R.id.card_view_movie_poster);
        imageView.setImageURI(mMovie.getPosterUri());

        Log.d(LOG_TAG, "Attempting to set TextView with Title: " + mMovie.getTitle());
        textView = (TextView) v.findViewById(R.id.content_movie_detail_title);
        textView.setText(mMovie.getTitle());

        Log.d(LOG_TAG, "Attempting to set TextView with Rel easeDate: " + mMovie.getReleaseDate());
        textView = (TextView) v.findViewById(R.id.content_movie_detail_release_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, y", Locale.US);
        String releaseDate = String.format(
                getString(R.string.movie_detail_release_date),
                sdf.format(mMovie.getReleaseDateAsDate()));
        textView.setText(releaseDate);

        Log.d(LOG_TAG, "Attempting to set TextView with VoteAverage: " + mMovie.getVoteAverage());
        textView = (TextView) v.findViewById(R.id.content_movie_detail_average_rating);

        String rating = String.format(
                getString(R.string.movie_detail_rating),
                Float.toString(mMovie.getVoteAverage()));
        textView.setText(rating);
        //textView.setText("Rating: " + Float.toString(mMovie.getVoteAverage()) + "/10");

        Log.d(LOG_TAG, "Attempting to set TextView with Overview: " + mMovie.getOverview());
        textView = (TextView) v.findViewById(R.id.content_movie_detail_text_view);
        textView.setText(mMovie.getOverview());

        return v;
    }

    /**
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /**
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
         */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /**
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
     */
}