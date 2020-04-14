package com.example.themoviemb.interface_movie;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieService {
    @GET("popular?api_key=525e39d1c3568cd23cdaf0a3674918fa&language=it-IT")
    Call<Result> getMovies();
}
