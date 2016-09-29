package com.example.jambo.jamboweather.util;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jambo on 2016/9/27.
 */

public interface WeatherApi {
    String BACE_URL = "https://api.heweather.com/x3/";

    @GET("weather")
    Observable<WeatherList> getWeather(@Query("city") String city, @Query("key") String key);
}
