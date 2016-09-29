package com.example.jambo.jamboweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.jambo.jamboweather.R;
import com.example.jambo.jamboweather.db.SelectedCityDBManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/9/26.
 */


public class CityManagerActivity extends Activity implements View.OnClickListener{
    private SelectedCityDBManager selectedCityDBManager;
    private ImageButton manager_add_city;
    private ImageButton manager_back_btn;
    private LinearLayout check_box_layout;
    private Button manager_delete_city;
    private List<View> removeCheckBoxs;
    private ArrayList<String> removeCities;
    private boolean isDelete = false;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager);
        selectedCityDBManager = new SelectedCityDBManager(this);
        initView();
        loadCityCheckBox();
    }

    public void initView(){
        //manager_add_city = (ImageButton) findViewById(R.id.city_manager_add_city);
        //manager_add_city.setOnClickListener(this);
        manager_back_btn = (ImageButton) findViewById(R.id.city_manager_btn_back);
        manager_back_btn.setOnClickListener(this);
        check_box_layout = (LinearLayout) findViewById(R.id.checkbox_layout);
        manager_delete_city = (Button) findViewById(R.id.city_manager_delete_city_button);
        manager_delete_city.setOnClickListener(this);
        manager_delete_city.setVisibility(View.INVISIBLE);
        removeCheckBoxs = new ArrayList<>();
        removeCities = new ArrayList<>();
    }

    private void loadCityCheckBox(){
        Cursor cursor = selectedCityDBManager.selectAllCity();
        if (cursor.moveToFirst()){
            do {
                String city_name = cursor.getString(cursor.getColumnIndex("name"));
                addCheckBox(city_name);
            }while (cursor.moveToNext());
        }
    }

    private void addCheckBox (final String city_name){
        final CheckBox checkBox = new CheckBox(getApplicationContext());
        checkBox.setText(city_name);
        checkBox.setTextColor(getResources().getColor(R.color.checkBoxText));
        check_box_layout.addView(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (checkBox.isChecked()){
                    manager_delete_city.setVisibility(View.VISIBLE);
                    removeCheckBoxs.add(checkBox);
                    removeCities.add(city_name);
                }else {
                    removeCheckBoxs.remove(checkBox);
                    removeCities.remove(city_name);
                    manager_delete_city.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override public void onClick(View v) {
        switch (v.getId()){
            //case R.id.city_manager_add_city:
            //    Intent intent1 = new Intent(CityManagerActivity.this,SearchCityActivity.class);
            //    startActivity(intent1);
            //    this.finish();
            //    break;
            case R.id.city_manager_btn_back:
                if (!isDelete){
                    removeCities.clear();
                }
                Intent intent = new Intent(this,MainActivity.class);
                intent.putStringArrayListExtra("removeCities",removeCities);
                setResult(2,intent);
                this.finish();
                break;
            case R.id.city_manager_delete_city_button:
                isDelete = true;
                for (View view : removeCheckBoxs){
                    check_box_layout.removeView(view);
                }
                break;

        }
    }
}
