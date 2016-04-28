package me.sethallen.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.Fragment.MainActivityFragment;
import me.sethallen.popularmovies.Model.Movie;
import me.sethallen.popularmovies.R;

/**
 * Created by Allense on 4/21/2016.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static String LOG_TAG = MovieAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovieList;
    private MainActivityFragment.OnMovieSelectedListener mListener;

    public MovieAdapter(MainActivityFragment.OnMovieSelectedListener listener) {
        this(new ArrayList<Movie>(), listener);
    }

    public MovieAdapter(ArrayList<Movie> movieList, MainActivityFragment.OnMovieSelectedListener listener) {
        this.mMovieList = movieList;
        this.mListener  = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View movieView = inflater.inflate(R.layout.card_view_movie, parent, false);

        return new ViewHolder(movieView, new ViewHolder.IClickableViewHolder() {

            @Override
            public void onMovieSelected(int position) {
                Movie movie = mMovieList.get(position);
                mListener.onMovieSelected(movie);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = this.mMovieList.get(position);

        holder.PosterImageView.setImageURI(movie.getPosterUri());
        //holder.TitleTextView.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.mMovieList.size();
    }

    public void appendItems(List<Movie> movies)
    {
        mMovieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mMovieList.clear();
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static String LOG_TAG = ViewHolder.class.getSimpleName();

        public SimpleDraweeView     PosterImageView;
        //public TextView             TitleTextView;
        public IClickableViewHolder ClickListener;

        public ViewHolder(View movieView, IClickableViewHolder listener) {
            super(movieView);

            ClickListener   = listener;
            PosterImageView = (SimpleDraweeView)movieView.findViewById(R.id.card_view_movie_poster);
            //TitleTextView   = (TextView)movieView.findViewById(R.id.card_view_movie_title);

            //PosterImageView.setOnClickListener(this);
            movieView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            ClickListener.onMovieSelected(getAdapterPosition());
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + TitleTextView.getText() + "'";
//        }

        public interface IClickableViewHolder
        {
            void onMovieSelected(int position);
        }
    }
}