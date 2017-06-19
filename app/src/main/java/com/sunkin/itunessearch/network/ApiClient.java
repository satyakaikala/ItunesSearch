package com.sunkin.itunessearch.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sunkin.itunessearch.Utility.BASE_URL;

/**
 * Created by skai0001 on 11/30/16.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
