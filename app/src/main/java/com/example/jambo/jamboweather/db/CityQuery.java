package com.example.jambo.jamboweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/9/27.
 */

public class CityQuery {
    private Context mContext;

    public CityQuery(Context context){
        mContext = context;
    }

    public List getCursor(SQLiteDatabase db){
        Cursor cursor = db.query("T_City",null,null,null,null,null,null);
        List cityNames = new ArrayList();
        while (cursor.moveToNext()){
            String city = cursor.getString(cursor.getColumnIndex("CityName"));
            cityNames.add(replaceCity(city));
        }
        return cityNames;
    }

    public List queryByLike(SQLiteDatabase db, String searchFilther){
        Cursor cursor = db.query("T_city",null,"CityName like '%" + searchFilther + "%'", null,null,null,null);
        List cityNames = new ArrayList();
        while (cursor.moveToNext()){
            String city = cursor.getString(cursor.getColumnIndex("CityName"));
            cityNames.add(replaceCity(city));
        }
        return cityNames;
    }


    private String replaceCity(String city){
       return city.replace("市","")
                  .replace("省","")
                  .replace("土家族苗族自治州","")
                  .replace("自治区","")
                  .replace("特别行政区", "")
                  .replace("地区", "")
                  .replace("盟", "");
    }
}
