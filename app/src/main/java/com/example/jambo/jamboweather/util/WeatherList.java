package com.example.jambo.jamboweather.util;

import com.example.jambo.jamboweather.mould.Weather;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/9/27.
 */

public class WeatherList {
    @SerializedName("HeWeather data service 3.0") @Expose

    public List<Weather> mWeathers = new ArrayList<>();
}
