package com.mkorp.newsboard.newsApi;

import com.mkorp.newsboard.newsApi.ApiResponse;
import com.mkorp.newsboard.newsApi.Category;
import com.mkorp.newsboard.newsApi.Country;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    @GET("top-headlines")
    Call<ApiResponse> getArticles(@Query("country") Country country, @Query("category") Category category, @Query("page") int page, @Query("apiKey") String apiKey);
    @GET("everything")
    Call<ApiResponse> searchArticles(@Query("q") String keyWords, @Query("page") int page, @Query("apiKey") String apiKey);
}
