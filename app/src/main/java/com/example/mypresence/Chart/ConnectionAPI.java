package com.example.mypresence.Chart;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionAPI {
    private static final String BASE_URL = "http://192.168.1.220:808/mypresence/";
    private static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if (retrofit==null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;
    }
}
