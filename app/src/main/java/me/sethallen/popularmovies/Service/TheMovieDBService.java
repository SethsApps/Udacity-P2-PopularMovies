package me.sethallen.popularmovies.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.sethallen.popularmovies.model.Configuration;
import me.sethallen.popularmovies.model.Images;
import me.sethallen.popularmovies.model.Movies;
import me.sethallen.popularmovies.model.Reviews;
import me.sethallen.popularmovies.model.Videos;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDBService {

    @GET("configuration")
    Call<Configuration> getConfiguration(@Query("api_key") String apiKey);

    @GET("movie/{movieEndpoint}")
    Call<Movies> getMovies(@Path("movieEndpoint") String movieEndpoint,
                           @Query("api_key")      String apiKey,
                           @Query("page")         String page);

    @GET("discover/movie/{imdbID}/images")
    Call<Images> getImages(@Path("imdbID")   String imdbID,
                           @Query("api_key") String apiKey);

    @GET("movie/{imdbID}/videos")
    Call<Videos> getVideos(@Path("imdbID")   String imdbID,
                           @Query("api_key") String apiKey);

    @GET("movie/{imdbID}/reviews")
    Call<Reviews> getReviews(@Path("imdbID")   String imdbID,
                             @Query("api_key") String apiKey);

    class Factory {

        public static TheMovieDBService create(String baseUrl) {

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(TheMovieDBService.class);
        }
    }
}