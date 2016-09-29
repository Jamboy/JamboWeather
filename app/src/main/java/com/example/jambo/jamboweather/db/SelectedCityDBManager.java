package com.example.jambo.jamboweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jambo on 2016/9/27.
 */

public class SelectedCityDBManager extends SQLiteOpenHelper{
    private Context mContext;
    private static final String DBNAME = "CityManager.db";
    private static final int DBVERSION = 1;
    private static final String TABLENAME = "CityManager";

    public SelectedCityDBManager(Context context){
        super(context,DBNAME,null,DBVERSION);
        mContext = context;
    }

    public static final String CREATE_CITYMANAGER = "create table CityManager("
        + "pagerIndex INTEGER PRIMARY KEY,"
        + "cityId int,"
        + "name text NOT NULL)";


    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITYMANAGER);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int addCity(String city_name, int city_id){
        if (!isExisted(city_name)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name",city_name);
            contentValues.put("cityId",city_id);
            return (int) db.insert(TABLENAME,null,contentValues);
        }
        return 0;
    }

    public void deleteCity(String city_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLENAME,"name = ?",new String[]{city_name});
    }

    public Cursor selectAllCity(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLENAME,null,null,null,null,null,"pagerIndex" + " ASC");
    }

    public int queryIdForName(String city_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLENAME,new String[]{"cityId"},"name = ?", new String[]{city_name},null,null,null,null);
        if (cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex("cityId"));
        }
        return 0;
    }

    public boolean isExisted(String city_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLENAME,null,"name = ?", new String[]{city_name},null,null,null,null);
        if (cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.trim().equals(city_name)) return true;
        }
        return false;
    }
}
