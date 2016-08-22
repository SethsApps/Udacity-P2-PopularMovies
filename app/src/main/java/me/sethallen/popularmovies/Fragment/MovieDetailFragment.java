package me.sethallen.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sethallen.popularmovies.adapter.ReviewAdapter;
import me.sethallen.popularmovies.adapter.VideoAdapter;
import me.sethallen.popularmovies.fresco.ImagePostProcessorForCollapsingToolbarLayoutDynamicTheme;
import me.sethallen.popularmovies.interfaces.IAsyncTaskResultHandler;
import me.sethallen.popularmovies.interfaces.IDisplayItems;
import me.sethallen.popularmovies.interfaces.IFavoriteStatusObserver;
import me.sethallen.popularmovies.model.Movie;
import me.sethallen.popularmovies.model.Review;
import me.sethallen.popularmovies.model.Reviews;
import me.sethallen.popularmovies.model.Video;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.model.Videos;
import me.sethallen.popularmovies.task.ManageFavoriteMovieTask;
import me.sethallen.popularmovies.task.TMDBLoaderTask;
import me.sethallen.popularmovies.utility.UriHelper;
import me.sethallen.popularmovies.fresco.WrapContentDraweeView;

public class MovieDetailFragment extends Fragment
        implements IAsyncTaskResultHandler {

    private static       String                   LOG_TAG           = MovieDetailFragment.class.getSimpleName();
    private static final String                   STATE_MOVIE       = "state-movie";
    private static final String                   STATE_MOVIE_WAS_FAVORITE_ALREADY = "state-movie-favorite-already";
    private static final String                   STATE_VIDEO_LIST  = "state-movie-list";
    private static final String                   STATE_REVIEW_LIST = "state-review-list";
    private              Movie                    mMovie;
    private              boolean                  mMovieWasFavoriteAlready;
    private              IFavoriteStatusObserver  mFavoriteStatusObserver;
    private              OnVideoSelectedListener  mVideoClickListener = null;
    private              GridLayoutManager        mVideoGridLayoutManager;
    private              VideoAdapter             mVideoAdapter;
    private              VideoDisplayer           mVideoDisplayer;
    private              OnReviewSelectedListener mReviewClickListener = null;
    private              GridLayoutManager        mReviewGridLayoutManager;
    private              ReviewAdapter            mReviewAdapter;
    private              ReviewDisplayer          mReviewDisplayer;
    private              MenuItem                 mShareMenuItem;
    private              ShareActionProvider      mShareActionProvider;

    @BindView(R.id.coordinator_layout)                  CoordinatorLayout       mCoordinatorLayout;
    @BindView(R.id.fab_favorite)                        FloatingActionButton    mFabFavorite;
    @BindView(R.id.collapsing_toolbar)                  CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.toolbar)                             Toolbar                 mToolbar;
    @BindView(R.id.backdrop_image_view)                 WrapContentDraweeView   mBackdropImage;
    @BindView(R.id.card_view_movie_poster)              WrapContentDraweeView   mPosterImage;
    @BindView(R.id.content_movie_detail_title)          TextView                mMovieTitle;
    @BindView(R.id.content_movie_detail_release_date)   TextView                mMovieReleaseDate;
    @BindView(R.id.content_movie_detail_average_rating) TextView                mVoteAverage;
    @BindView(R.id.content_movie_detail_text_view)      TextView                mMovieOverview;
    @BindView(R.id.trailers_section)                    LinearLayoutCompat      mTrailersSection;
    @BindView(R.id.recycler_view_videos)                RecyclerView            mVideoRecyclerView;
    @BindView(R.id.reviews_section)                     LinearLayoutCompat      mReviewsSection;
    @BindView(R.id.recycler_view_reviews)               RecyclerView            mReviewRecyclerView;

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
        args.putParcelable(STATE_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle args = new Bundle();

        args.putParcelable(STATE_MOVIE,                   mMovie);
        args.putBoolean(STATE_MOVIE_WAS_FAVORITE_ALREADY, mMovieWasFavoriteAlready);
        args.putSerializable(STATE_VIDEO_LIST,            mVideoAdapter.getVideoList());

        outState.putAll(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, v);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(mToolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if (getArguments() == null)
        {
            return v;
        }
        else
        {
            mMovie                   = getArguments().getParcelable(STATE_MOVIE);
            mMovieWasFavoriteAlready = mMovie.GetIsFavorite();
        }

        final MovieDetailFragment thisClass = this;

        setFavoriteButtonDrawable();

        mFabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageFavoriteMovieTask task
                        = new ManageFavoriteMovieTask(getActivity(), thisClass);
                task.execute(mMovie);
            }
        });

        Log.d(LOG_TAG, "Backdrop Uri: " + mMovie.getBackdropUriString());

        Log.d(LOG_TAG, "Attempting to set Backdrop ImageView with Backdrop Path: " + mMovie.getBackdropPath());
        // Build an ImageRequest using the customer PostProcessor that
        // dynamically themes the CollapsingToolBarLayout so the theme will be based
        // on colors from the Backdrop image.
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mMovie.getBackdropUri())
                .setPostprocessor(new ImagePostProcessorForCollapsingToolbarLayoutDynamicTheme(mCollapsingToolbar))
                .build();
        // Create the drawee controller that uses the customer ImageRequest from above to
        // load the BackdropImage and apply the dynamic themeing to the toolbar layout based on the image.
        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mBackdropImage.getController())
                .build();
        // Set the controller, which causes the image to be loaded and the layout to be themed.
        mBackdropImage.setController(controller);

        Log.d(LOG_TAG, "Attempting to set Poster ImageView with Poster Path: " + mMovie.getPosterPath());
        mPosterImage.setImageURI(mMovie.getPosterUri());

        mMovieTitle.setText(mMovie.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, y", Locale.US);
        String releaseDate = String.format(getString(R.string.movie_detail_release_date), sdf.format(mMovie.getReleaseDateAsDate()));
        mMovieReleaseDate.setText(releaseDate);

        String rating = String.format(getString(R.string.movie_detail_rating), Float.toString(mMovie.getVoteAverage()));
        mVoteAverage.setText(rating);

        mMovieOverview.setText(mMovie.getOverview());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_movie_detail_fragment, menu);
        mShareMenuItem       = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShareMenuItem);

        mShareMenuItem.setVisible(false);
        if (mVideoAdapter != null)
        {
            List<Video> videoList = mVideoAdapter.getVideoList();
            if (videoList != null && !videoList.isEmpty())
            {
                updateShareMovieAction(videoList.get(0));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void updateShareMovieAction(Video video) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        String shareMessage = "Look at this trailer I found for "
                + mMovie.getOriginalTitle()
                + "\n"
                + UriHelper.getYouTubeUri(video.getKey());

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        if (mShareMenuItem != null && mShareActionProvider != null)
        {
            mShareMenuItem.setVisible(true);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeVideosGrid();
        initializeReviewsGrid();

        if (savedInstanceState != null) {
            mMovie                   = savedInstanceState.getParcelable(STATE_MOVIE);
            mMovieWasFavoriteAlready = savedInstanceState.getBoolean(STATE_MOVIE_WAS_FAVORITE_ALREADY);
            List<Video> videoList    = (ArrayList<Video>) savedInstanceState.getSerializable(STATE_VIDEO_LIST);
            List<Review> reviewList  = (ArrayList<Review>) savedInstanceState.getSerializable(STATE_REVIEW_LIST);

            appendVideos(videoList);
            appendReviews(reviewList);
        }
        else {
            loadVideos();
            loadReviews();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnVideoSelectedListener) {
            mVideoClickListener = (OnVideoSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVideoSelectedListener");
        }

        if (context instanceof OnReviewSelectedListener) {
            mReviewClickListener = (OnReviewSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnReviewSelectedListener");
        }

        if (context instanceof IFavoriteStatusObserver) {
            mFavoriteStatusObserver = (IFavoriteStatusObserver) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFavoriteStatusObserver");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mVideoClickListener     = null;
        mReviewClickListener    = null;
        mFavoriteStatusObserver = null;
    }

    @Override
    public void onSuccess(String result)
    {
        if (result.endsWith(ManageFavoriteMovieTask.RESULT_ADDED_MOVIE_TO_FAVORITES))
        {
            mMovie.SetIsFavorite(true);
        }
        else
        {
            mMovie.SetIsFavorite(false);
        }

        Log.d(LOG_TAG, "Calling mFavoriteStatusObserver favoriteStatusChanged: Already="
                + mMovieWasFavoriteAlready
                + " status="
                + mMovie.GetIsFavorite());
        mFavoriteStatusObserver.favoriteStatusChanged(mMovieWasFavoriteAlready != mMovie.GetIsFavorite());

        setFavoriteButtonDrawable();

        Snackbar.make(mCoordinatorLayout, mMovie.getTitle() + " " + result, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onFailure()
    {

    }

    private void setFavoriteButtonDrawable()
    {
        Drawable drawableFavorite;
        if (mMovie.GetIsFavorite())
        {
            drawableFavorite = ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite);
        }
        else
        {
            drawableFavorite = ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border);
        }

        mFabFavorite.setImageDrawable(drawableFavorite);
    }

    public void loadVideos() {
        int retryCount = 0;
        do {
            try {
                TMDBLoaderTask<Videos, Video> videoLoaderTask = new TMDBLoaderTask<>(this, mVideoDisplayer);
                TMDBLoaderTask.LoaderArgs args = videoLoaderTask.new LoaderArgs(String.valueOf(mMovie.getId()),
                                                                                 "getVideos");

                videoLoaderTask.execute(args);
                break;
            }
            catch (Exception ex)
            {
                retryCount++;
            }
        } while (retryCount < 3);
    }

    public void loadReviews() {
        int retryCount = 0;
        do {
            try {
                TMDBLoaderTask<Reviews, Review> reviewLoaderTask = new TMDBLoaderTask<>(this, mReviewDisplayer);
                TMDBLoaderTask.LoaderArgs args = reviewLoaderTask.new LoaderArgs(String.valueOf(mMovie.getId()),
                                                                                 "getReviews");
                reviewLoaderTask.execute(args);
                break;
            }
            catch (Exception ex)
            {
                retryCount++;
            }
        } while (retryCount < 3);
    }

    private void initializeVideosGrid()
    {
        // instantiate the grid layout manager if it's null and set it on the objects that depend on it,
        // otherwise ensure that dependent objects have it if it already existed.
        if (mVideoGridLayoutManager == null) {
            mVideoGridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
            mVideoRecyclerView.setLayoutManager(mVideoGridLayoutManager);
        } else if (mVideoRecyclerView.getLayoutManager() == null) {
            mVideoRecyclerView.setLayoutManager(mVideoGridLayoutManager);
        }

        // instantiate the adapter if it's null and set it on the objects that depend on it,
        // otherwise ensure that dependent objects have it if it already existed.
        if (mVideoAdapter == null) {
            mVideoAdapter = new VideoAdapter(mVideoClickListener);
            mVideoRecyclerView.setAdapter(mVideoAdapter);
        } else {
            if (mVideoRecyclerView.getAdapter() == null) {
                mVideoRecyclerView.setAdapter(mVideoAdapter);
            }
        }

        if (mVideoDisplayer == null) {
            mVideoDisplayer = new VideoDisplayer(this);
        }
    }

    private void initializeReviewsGrid()
    {
        // instantiate the grid layout manager if it's null and set it on the objects that depend on it,
        // otherwise ensure that dependent objects have it if it already existed.
        if (mReviewGridLayoutManager == null) {
            mReviewGridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
            mReviewRecyclerView.setLayoutManager(mReviewGridLayoutManager);
        } else if (mReviewRecyclerView.getLayoutManager() == null) {
            mReviewRecyclerView.setLayoutManager(mReviewGridLayoutManager);
        }

        // instantiate the adapter if it's null and set it on the objects that depend on it,
        // otherwise ensure that dependent objects have it if it already existed.
        if (mReviewAdapter == null) {
            mReviewAdapter = new ReviewAdapter(this.getContext(), mReviewClickListener);
            mReviewRecyclerView.setAdapter(mReviewAdapter);
        } else {
            if (mReviewRecyclerView.getAdapter() == null) {
                mReviewRecyclerView.setAdapter(mReviewAdapter);
            }
        }

        if (mReviewDisplayer == null) {
            mReviewDisplayer = new ReviewDisplayer(this);
        }
    }

    private void appendVideos(List<Video> items) {
        // Hide the trailers section if there are no items to show
        if (items == null || items.isEmpty()) {
            mTrailersSection.setVisibility(View.GONE);
            return;
        }

        mVideoAdapter.clear();
        mVideoAdapter.appendItems(items);
        updateShareMovieAction(items.get(0));

        // Show the trailers section now that we've added them to the Video Adapter
        mTrailersSection.setVisibility(View.VISIBLE);
    }

    private void appendReviews(List<Review> items) {
        // Hide the reviews section if there are no items to show
        if (items == null || items.isEmpty()) {
            mReviewsSection.setVisibility(View.GONE);
            return;
        }

        mReviewAdapter.clear();
        mReviewAdapter.appendItems(items);

        // Show the reviews section now that we've added them to the Video Adapter
        mReviewsSection.setVisibility(View.VISIBLE);
    }

    public static class VideoDisplayer implements IDisplayItems<Video> {
        private MovieDetailFragment mMovieDetailFragment;

        public VideoDisplayer(MovieDetailFragment movieDetailFragment) {
            this.mMovieDetailFragment = movieDetailFragment;
        }

        @Override
        public void appendItems(List<Video> items) {
            mMovieDetailFragment.appendVideos(items);
        }
    }

    public static class ReviewDisplayer implements IDisplayItems<Review> {
        private MovieDetailFragment mMovieDetailFragment;

        public ReviewDisplayer(MovieDetailFragment movieDetailFragment) {
            this.mMovieDetailFragment = movieDetailFragment;
        }

        @Override
        public void appendItems(List<Review> items) {
            mMovieDetailFragment.appendReviews(items);
        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(Video video);
    }

    public interface OnReviewSelectedListener {
        void onReviewSelected(Review review);
    }
}