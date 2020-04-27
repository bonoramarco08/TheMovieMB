package com.example.themoviemb.interface_movie;
import com.example.themoviemb.data.models.Result;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {


    @GET("1?page=1&api_key=525e39d1c3568cd23cdaf0a3674918fa&language=it-IT")
    Call<Result> getMovies();
    @GET("3/discover/movie?api_key=525e39d1c3568cd23cdaf0a3674918fa")
    Call<Result> getMoviesPage(@Query("page") int page , @Query("primary_release_date.lte") String releaseDate ,@Query("language") String lingua);
}
