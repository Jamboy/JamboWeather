package com.example.jambo.jamboweather.util;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jambo on 2016/9/27.
 */

public class HttpUtil {
    public static Retrofit mRetrofit = null;
    public static WeatherApi mWeatherApi = null;

    public static void init(){
        mRetrofit = new Retrofit.Builder()
            .baseUrl(WeatherApi.BACE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

        mWeatherApi = mRetrofit.create(WeatherApi.class);
    }

    public static WeatherApi getmWeatherApi(){
        if (mWeatherApi != null) return mWeatherApi;
        init();
        return getmWeatherApi();
    }


}
