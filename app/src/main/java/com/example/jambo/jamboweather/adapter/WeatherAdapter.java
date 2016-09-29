package com.example.jambo.jamboweather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.jambo.jamboweather.R;
import com.example.jambo.jamboweather.mould.Weather;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Jambo on 2016/6/7.
 */
public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private Weather mWeather = null;
    private static final int TYPE_NOW = 0;
    private static final int TYPE_HOURLY = 3;
    private static final int TYPE_SUGGESTION = 2;
    private static final int TYPE_DAYLY = 1;

    public WeatherAdapter(Context context, Weather weather){
        this.mContext = context;
        this.mWeather = weather;
    }

    //public WeatherAdapter(Context context){
    //    this.mContext = context;
    //}
    //
    //public  void addWeatherData(Weather weather){
    //    this.mWeather = weather;
    //}

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_NOW) {
            return TYPE_NOW;
        }
        if (position == TYPE_HOURLY){
            return TYPE_HOURLY;
        }
        if (position == TYPE_SUGGESTION){
            return TYPE_SUGGESTION;
        }
        if (position == TYPE_DAYLY){
            return TYPE_DAYLY;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeatherNowHolder){
            ((WeatherNowHolder) holder).now_tmp.setText(mWeather.now.tmp +"°");
            ((WeatherNowHolder) holder).now_desc.setText(mWeather.now.cond.txt);
            if (judgeWeather(mWeather.now.cond.txt).equals("雨")){
                ((WeatherNowHolder) holder).now_tmp_pic.setImageResource(R.drawable.rain);
            }else {
                ((WeatherNowHolder) holder).now_tmp_pic.setImageResource(R.drawable.sun);
            }
            ((WeatherNowHolder) holder).now_tmp_max.setText("↑" + mWeather.dailyForecast.get(0).tmp.max+"°");
            ((WeatherNowHolder) holder).now_tmp_min.setText("↓" + mWeather.dailyForecast.get(0).tmp.min + "°");
        }

        if (holder instanceof WeatherHourHolder){
            ((WeatherHourHolder) holder).cardViewHourly.setCardBackgroundColor(R.color.cardview_background);
            for(int i =0; i < mWeather.hourlyForecast.size(); i++){
                String data = mWeather.hourlyForecast.get(i).date;
                ((WeatherHourHolder) holder).nowTime[i].setText(data.substring(data.length()-5));
                ((WeatherHourHolder) holder).nowPop[i].setText("降雨几率:" + mWeather.hourlyForecast.get(i).pop +"%");
                ((WeatherHourHolder) holder).nowTmp[i].setText( "↑ " + mWeather.hourlyForecast.get(i).tmp + "℃");
            }
        }
        if (holder instanceof WeatherSuggestionHolder){
            ((WeatherSuggestionHolder) holder).cardViewSuggestion.setCardBackgroundColor(R.color.cardview_background);
            ((WeatherSuggestionHolder) holder).suggestionDrsgName.setText("穿衣指数");
            ((WeatherSuggestionHolder) holder).suggestionDrsgDesc.setText(mWeather.suggestion.drsg.txt);
            ((WeatherSuggestionHolder) holder).suggestionUvName.setText("防晒指数");
            ((WeatherSuggestionHolder) holder).suggestionUvDesc.setText(mWeather.suggestion.uv.txt);
            ((WeatherSuggestionHolder) holder).suggestionTravName.setText("旅游指数");
            ((WeatherSuggestionHolder) holder).suggestionTravDesc.setText(mWeather.suggestion.trav.txt);
            ((WeatherSuggestionHolder) holder).suggestionSportName.setText("运动指数");
            ((WeatherSuggestionHolder) holder).getSuggestionSportDesc.setText(mWeather.suggestion.sport.txt);
        }
        if (holder instanceof WeatherDaylyHolder){
            try {
                ((WeatherDaylyHolder) holder).cardViewDayly.setCardBackgroundColor(R.color.cardview_background);
                for (int i = 0; i < mWeather.dailyForecast.size(); i++){
                    String date = mWeather.dailyForecast.get(i).date;
                    if (i == 0){
                        ((WeatherDaylyHolder) holder).day[0].setText("今天");
                    }else if (i == 1){
                        ((WeatherDaylyHolder) holder).day[1].setText("明天");
                    }else {
                        ((WeatherDaylyHolder) holder).day[i].setText(dayForWeek(date));
                    }
                    ((WeatherDaylyHolder) holder).description[i].setText(mWeather.dailyForecast.get(i).cond.txtD);
                    ((WeatherDaylyHolder) holder).maxTmp[i].setText(mWeather.dailyForecast.get(i).tmp.max + "℃");
                    ((WeatherDaylyHolder) holder).minTmp[i].setText(mWeather.dailyForecast.get(i).tmp.min + "℃");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NOW){
            return new WeatherNowHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_now_item_text,parent,false));
        }
        if (viewType == TYPE_HOURLY){
            return new WeatherHourHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_hourly_forecast_item_text,parent,false));
        }
        if (viewType == TYPE_SUGGESTION){
            return new WeatherSuggestionHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_suggestion_item_text,parent,false));
        }
        if (viewType == TYPE_DAYLY){
            return new WeatherDaylyHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_dayly_forecast_itme_text,parent,false));
        }
        return null;
    }

    /**
     * 当前天气 一个city 一个tmp
     */

    class WeatherNowHolder extends RecyclerView.ViewHolder{

        TextView now_tmp;
        TextView now_tmp_max;
        TextView now_tmp_min;
        TextView now_desc;
        ImageView now_tmp_pic;

        public WeatherNowHolder(View itemView) {
            super(itemView);
            now_tmp = (TextView) itemView.findViewById(R.id.now_tmp);
            now_tmp_max = (TextView) itemView.findViewById(R.id.now_tmp_max);
            now_tmp_min = (TextView) itemView.findViewById(R.id.now_tmp_min);
            now_desc = (TextView) itemView.findViewById(R.id.now_tmp_desc);
            now_tmp_pic = (ImageView) itemView.findViewById(R.id.now_tmp_image);
        }
    }

    /**
     * 每小时天气
     */
    class WeatherHourHolder extends RecyclerView.ViewHolder{
        int size = mWeather.hourlyForecast.size();
        private LinearLayout weatherHourlyForecastItem;
        private TextView[] nowTime = new TextView[size];
        private TextView[] nowPop = new TextView[size];
        private TextView[] nowTmp = new TextView[size];
        CardView cardViewHourly;

        public WeatherHourHolder(View itemView) {
            super(itemView);
            cardViewHourly = (CardView) itemView.findViewById(R.id.cardview_hourly_forecast_item);
            weatherHourlyForecastItem = (LinearLayout) itemView.findViewById(R.id.hourly_forecast_item);

            for (int i = 0; i < size; i++){
                View view = View.inflate(mContext,R.layout.weather_hourly_forecast_line_text,null);
                nowTime[i] = (TextView) view.findViewById(R.id.item_hourly_now_time);
                nowPop[i] = (TextView) view.findViewById(R.id.item_hourly_pop);
                nowTmp[i] = (TextView) view.findViewById(R.id.item_hourly_now_tmp);
                weatherHourlyForecastItem.addView(view);
            }
        }
    }

    /**
     *当日建议
     */
    class WeatherSuggestionHolder extends RecyclerView.ViewHolder{
        TextView suggestionDrsgName;
        TextView suggestionDrsgDesc;
        TextView suggestionUvName;
        TextView suggestionUvDesc;
        TextView suggestionTravName;
        TextView suggestionTravDesc;
        TextView suggestionSportName;
        TextView getSuggestionSportDesc;
        CardView cardViewSuggestion;

        public WeatherSuggestionHolder(View itemView) {
            super(itemView);
            cardViewSuggestion = (CardView) itemView.findViewById(R.id.cardview_suggestion_item);
            suggestionDrsgName = (TextView) itemView.findViewById(R.id.item_suggestion_drsg_brg_text);
            suggestionDrsgDesc = (TextView) itemView.findViewById(R.id.item_suggestion_drsg_txt_text);
            suggestionUvName = (TextView) itemView.findViewById(R.id.item_suggestion_uv_brf_text);
            suggestionUvDesc = (TextView) itemView.findViewById(R.id.item_suggestion_uv_txt_text);
            suggestionTravName = (TextView) itemView.findViewById(R.id.item_suggestion_truv_brf_text);
            suggestionTravDesc = (TextView) itemView.findViewById(R.id.item_suggestion_truv_txt_text);
            suggestionSportName = (TextView) itemView.findViewById(R.id.item_suggestion_sport_brf_text);
            getSuggestionSportDesc = (TextView) itemView.findViewById(R.id.item_suggestion_sport_txt_text);
        }
    }


    /**
     * 一周天气预报
     */
    class WeatherDaylyHolder extends RecyclerView.ViewHolder{
        int size = mWeather.dailyForecast.size();
        private LinearLayout weatherDaylyForecastItem;
        private TextView[] day = new TextView[size];
        private TextView[] description = new TextView[size];
        private TextView[] maxTmp = new TextView[size];
        private TextView[] minTmp = new TextView[size];
        CardView cardViewDayly;

        public WeatherDaylyHolder(View itemView) {
            super(itemView);
            cardViewDayly = (CardView) itemView.findViewById(R.id.cardview_dayly_forecast_item);
            weatherDaylyForecastItem = (LinearLayout) itemView.findViewById(R.id.dayly_forecast_item);
            for (int i = 0; i < size; i++){
                View view = View.inflate(mContext,R.layout.weather_dayly_forecast_line_text,null);
                day[i] = (TextView) view.findViewById(R.id.day);
                description[i] = (TextView) view.findViewById(R.id.description);
                maxTmp[i] = (TextView) view.findViewById(R.id.max_temp);
                minTmp[i] = (TextView) view.findViewById(R.id.min_temp);
                weatherDaylyForecastItem.addView(view);
            }
        }
    }

    public String dayForWeek(String Time) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(Time));
        int dayForWeek = 0;
        String week = "";
        dayForWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayForWeek){
            case 1:
                week = "星期天";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    private String judgeWeather(String weather){
        for (int i = 0; i < weather.length(); i++){
            String s = String.valueOf(weather.charAt(i));
            if (s.equals("雨")){
                return "雨";
            }
        }
        return "云";
    }
}
