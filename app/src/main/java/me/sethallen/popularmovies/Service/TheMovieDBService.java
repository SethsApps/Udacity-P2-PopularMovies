package me.sethallen.popularmovies.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.sethallen.popularmovies.Model.Configuration;
import me.sethallen.popularmovies.Model.Images;
import me.sethallen.popularmovies.Model.MovieResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Allense on 4/21/2016.
 */
public interface TheMovieDBService {

    @GET("configuration")
    Call<Configuration> getConfiguration(@Query("api_key") String apiKey);

    @GET("movie/{movieEndpoint}")
    Call<MovieResponse> getMovies(@Path("movieEndpoint") String movieEndpoint,
                                  @Query("api_key")      String apiKey,
                                  @Query("page")         String page);

    @GET("discover/movie/{imdbID}/images")
    Call<Images> getImages(@Path("imdbID")   String imdbID,
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