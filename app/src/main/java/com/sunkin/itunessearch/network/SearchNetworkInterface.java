package com.sunkin.itunessearch.network;

import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.data.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by skai0001 on 4/10/17.
 */

public interface SearchNetworkInterface {

    //https://itunes.apple.com/search?term=jack+johnson&entity=musicVideo
    @GET("search")
    Call<SearchResponse> getSearchResults(@Query("term") String query, @Query("entity") String entity);
}
