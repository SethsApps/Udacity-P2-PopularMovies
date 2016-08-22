package me.sethallen.popularmovies.interfaces;

import java.util.List;

import me.sethallen.popularmovies.model.Movie;

public interface IDisplayItems<T> {
    void appendItems(List<T> items);
}