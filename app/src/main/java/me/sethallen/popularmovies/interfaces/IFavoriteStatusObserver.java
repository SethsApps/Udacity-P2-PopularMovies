package me.sethallen.popularmovies.interfaces;

public interface IFavoriteStatusObserver {
    void favoriteStatusChanged(Integer movieId, boolean statusChanged);
}
