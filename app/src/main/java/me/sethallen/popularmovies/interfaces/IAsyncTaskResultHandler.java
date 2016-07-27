package me.sethallen.popularmovies.interfaces;

public interface IAsyncTaskResultHandler {
    void onSuccess(String result);
    void onFailure();
}
