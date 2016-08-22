package me.sethallen.popularmovies.interfaces;

import java.util.List;

public interface ITMDBResponse<T> {
    void setResults(List<T> results);
    List<T> getResults();
}
