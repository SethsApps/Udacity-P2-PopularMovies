package me.sethallen.popularmovies.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.fragment.MovieDetailFragment;
import me.sethallen.popularmovies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private ArrayList<Review>                            mReviewList;
    private MovieDetailFragment.OnReviewSelectedListener mListener;
    private Context                                      mContext;

    public ReviewAdapter(Context context, MovieDetailFragment.OnReviewSelectedListener listener) {
        this(context, new ArrayList<Review>(), listener);
    }

    public ReviewAdapter(Context context, ArrayList<Review> reviewList, MovieDetailFragment.OnReviewSelectedListener listener) {
        this.mContext    = context;
        this.mReviewList = reviewList;
        this.mListener   = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reviewView = inflater.inflate(R.layout.card_view_review, parent, false);
        reviewView.setBackgroundColor(Color.WHITE);

        return new ViewHolder(reviewView, new ViewHolder.IClickableViewHolder() {

            @Override
            public void onReviewSelected(int position) {
                Review review = mReviewList.get(position);
                mListener.onReviewSelected(review);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Review review = this.mReviewList.get(position);

        String author = String.format(mContext.getString(R.string.review_author), review.getAuthor());
        holder.AuthorTextView.setText(author);
        holder.ContentTextView.setText(review.getContent());
    }

    public ArrayList<Review> getReviewList() { return this.mReviewList; }

    @Override
    public int getItemCount() {
        return this.mReviewList.size();
    }

    public void appendItems(List<Review> reviews)
    {
        mReviewList.addAll(reviews);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mReviewList.clear();
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static String LOG_TAG = ViewHolder.class.getSimpleName();

        public TextView             AuthorTextView;
        public TextView             ContentTextView;
        public IClickableViewHolder ClickListener;

        public ViewHolder(View reviewView, IClickableViewHolder listener) {
            super(reviewView);

            ClickListener   = listener;
            AuthorTextView  = (TextView)reviewView.findViewById(R.id.card_view_review_author);
            ContentTextView = (TextView)reviewView.findViewById(R.id.card_view_review_content);

            reviewView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            ClickListener.onReviewSelected(getAdapterPosition());
        }

        public interface IClickableViewHolder
        {
            void onReviewSelected(int position);
        }
    }
}