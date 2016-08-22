package me.sethallen.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;

import me.sethallen.popularmovies.fragment.MovieDetailFragment;
import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.model.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private static String LOG_TAG = VideoAdapter.class.getSimpleName();

    private ArrayList<Video>                            mVideoList;
    private MovieDetailFragment.OnVideoSelectedListener mListener;

    public VideoAdapter(MovieDetailFragment.OnVideoSelectedListener listener) {
        this(new ArrayList<Video>(), listener);
    }

    public VideoAdapter(ArrayList<Video> videoList, MovieDetailFragment.OnVideoSelectedListener listener) {
        this.mVideoList = videoList;
        this.mListener  = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View videoView = inflater.inflate(R.layout.card_view_video, parent, false);

        return new ViewHolder(videoView, new ViewHolder.IClickableViewHolder() {

            @Override
            public void onVideoSelected(int position) {
                Video video = mVideoList.get(position);
                mListener.onVideoSelected(video);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Video video = this.mVideoList.get(position);

        holder.VideoThumbnailImageView.setImageURI(getUri(video.getKey()));
        holder.NameTextView.setText(video.getName());
    }

    private Uri getUri(String videoKey)
    {
        Uri uri = Uri.parse("http://img.youtube.com/vi")
                .buildUpon()
                .appendPath(videoKey)
                .appendPath("mqdefault.jpg")
                .build();

        Log.d(LOG_TAG, "URI was: " + uri.toString());
        return uri;
    }

    public ArrayList<Video> getVideoList() { return this.mVideoList; }

    @Override
    public int getItemCount() {
        return this.mVideoList.size();
    }

    public void appendItems(List<Video> videos)
    {
        mVideoList.addAll(videos);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mVideoList.clear();
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static String LOG_TAG = ViewHolder.class.getSimpleName();

        public SimpleDraweeView     VideoThumbnailImageView;
        public ImageView            YoutubePlayButton;
        public TextView             NameTextView;
        public IClickableViewHolder ClickListener;

        public ViewHolder(View videoView, IClickableViewHolder listener) {
            super(videoView);

            ClickListener           = listener;
            VideoThumbnailImageView = (SimpleDraweeView)videoView.findViewById(R.id.card_view_video_thumbnail);
            YoutubePlayButton       = (ImageView) videoView.findViewById(R.id.button_youtube_player);
            NameTextView            = (TextView)videoView.findViewById(R.id.card_view_video_name);

            videoView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            ClickListener.onVideoSelected(getAdapterPosition());
        }

        public interface IClickableViewHolder
        {
            void onVideoSelected(int position);
        }
    }
}