package me.sethallen.popularmovies.utility;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RetrofitHelper {

    private static String LOG_TAG = RetrofitHelper.class.getSimpleName();

    public static <T> T ExecuteCall(Call<T> apiCall)
    {
        Response<T> apiResponse;
        try {
            apiResponse = apiCall.execute();
        }
        catch (IOException ioEx) {
            Log.e(LOG_TAG, "IOException attempting to execute api call", ioEx);
            return null;
        }

        return HandleCallResponse(apiResponse);
    }

    public static <T> T HandleCallResponse(Response<T> apiResponse)
    {
        if (!apiResponse.isSuccessful()) {
            Log.d(LOG_TAG, "api call was unsuccessful");
            return null;
        }

        T response = apiResponse.body();

        if (response == null) {
            Log.d(LOG_TAG, "response from api call is null");
            return null;
        }

        return response;
    }
}