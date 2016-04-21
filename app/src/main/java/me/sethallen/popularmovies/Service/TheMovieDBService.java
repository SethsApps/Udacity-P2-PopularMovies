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

    //http://api.themoviedb.org/3/discover/movie?api_key=b2cb5be395a3f5097f4961438a87a72b&sort_by=popularity.desc
//    @GET("3/discover/movie?api_key={TMDbAPIKey}&sort_by=popularity.desc")
//    Observable<List<Movie>> movies(@Path("TMDbAPIKey") String TMDbAPIKey);

    @GET("configuration")
    Call<Configuration> getConfiguration(@Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<MovieResponse> getMovies(@Query("api_key") String apiKey,
                                  @Query("sort_by") String sortBy,
                                  @Query("page")    String page);

    @GET("discover/movie/{imdbid}/images")
    Call<Images> getImages(@Path("imdbid") String imdbid,
                           @Query("api_key") String apiKey);

//    @GET("movie")
//    Observable<MovieResponse> getMoviesRx(@Query("sort_by") String sortKey,
//                                          @Query("api_key") String apiKey);

    //https://api.themoviedb.org/3/movie/550?api_key=
//    @GET
//    Observable<Movie> movieFromUrl(@Url String movieUrl);


    class Factory {

        public static TheMovieDBService create(String baseUrl) {

//            OkHttpClient client = new OkHttpClient();
//            client.interceptors().add(new Interceptor() {
//                @Override
//                public Response intercept(Interceptor.Chain chain) throws IOException {
//                    Response response = chain.proceed(chain.request());
//                    // Do anything with response here
//                    return response;
//                }
//            });

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            /*
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
                    */

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    //.client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(TheMovieDBService.class);
        }
    }
}