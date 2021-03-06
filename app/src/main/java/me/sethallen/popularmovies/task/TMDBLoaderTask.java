package me.sethallen.popularmovies.task;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.sethallen.popularmovies.R;
import me.sethallen.popularmovies.interfaces.IDisplayItems;
import me.sethallen.popularmovies.interfaces.ITMDBResponse;
import me.sethallen.popularmovies.model.Video;
import me.sethallen.popularmovies.model.Videos;
import me.sethallen.popularmovies.service.TheMovieDBService;
import me.sethallen.popularmovies.utility.RetrofitHelper;
import retrofit2.Call;
import retrofit2.Response;

public class TMDBLoaderTask<TResponse extends ITMDBResponse<TItem>, TItem>
        extends AsyncTask<TMDBLoaderTask.LoaderArgs, Void, List<TItem>> {

    private static String               LOG_TAG         = TMDBLoaderTask.class.getSimpleName();
    private        IDisplayItems<TItem> mItemDisplayer;

    public TMDBLoaderTask(IDisplayItems<TItem> itemDisplayer) {
        mItemDisplayer = itemDisplayer;
    }

    @Override
    protected List<TItem> doInBackground(TMDBLoaderTask.LoaderArgs... params) {

        LoaderArgs args = params[0];

        final TheMovieDBService tmdbApi = TheMovieDBService.Factory.create(args.getTmdbBaseUrl());

        Log.d(LOG_TAG, "attempting to call " + args.getApiMethodName()
                + "() with key " + args.getTmdbApiKey()
                + " and IMDB Id " + args.getImdbId());

        TResponse response = null;
        try {
            Class<?>[] parameterTypes = {String.class, String.class};
            Method method = TheMovieDBService.class.getDeclaredMethod(args.getApiMethodName(), parameterTypes);
            @SuppressWarnings("unchecked")
            Call<TResponse> apiCall = (Call<TResponse>)method.invoke(tmdbApi, args.getImdbId(), args.getTmdbApiKey());
            Response<TResponse> apiCallResponse = apiCall.execute();
            response = RetrofitHelper.HandleCallResponse(apiCallResponse);
        }
        catch (NoSuchMethodException nsme)
        {
            Log.e(LOG_TAG, "Failed to load items", nsme);
        }
        catch (IllegalAccessException iae)
        {
            Log.e(LOG_TAG, "Failed to load items", iae);
        }
        catch (InvocationTargetException ite)
        {
            Log.e(LOG_TAG, "Failed to load items", ite);
        }
        catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Failed to load items", ioe);
        }

        if (response == null) {
            Log.d(LOG_TAG, "response from " + args.getApiMethodName() + "() call is null");
            return null;
        }

        List<TItem> resultList = response.getResults();
        if (resultList == null) {
            Log.d(LOG_TAG, "results list from " + args.getApiMethodName() + "() call is null");
            return null;
        } else {
            Log.d(LOG_TAG, "results list from " + args.getApiMethodName() + "() call size is: " + resultList.size());
        }

        return resultList;
    }

    @Override
    protected void onPostExecute(List<TItem> results) {
        super.onPostExecute(results);

        mItemDisplayer.appendItems(results);
    }

    public class LoaderArgs
    {
        private String mTmdbApiKey;
        private String mTmdbBaseUrl;
        private String mImdbId;
        private String mApiMethodName;

        public String getTmdbApiKey() {
            return mTmdbApiKey;
        }
        public String getTmdbBaseUrl() {
            return mTmdbBaseUrl;
        }
        public String getImdbId() {
            return mImdbId;
        }
        public String getApiMethodName() {
            return mApiMethodName;
        }

        public LoaderArgs(String tmdbApiKey, String tmdbBaseUrl, String imdbId, String apiMethodName) {
            this.mTmdbApiKey    = tmdbApiKey;
            this.mTmdbBaseUrl   = tmdbBaseUrl;
            this.mImdbId        = imdbId;
            this.mApiMethodName = apiMethodName;
        }
    }
}