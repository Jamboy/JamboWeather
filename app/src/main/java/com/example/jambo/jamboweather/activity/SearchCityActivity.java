package com.example.jambo.jamboweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.jambo.jamboweather.R;
import com.example.jambo.jamboweather.db.CityQuery;
import com.example.jambo.jamboweather.db.DBManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/9/26.
 */

public class SearchCityActivity extends Activity implements SearchView.OnCloseListener,
    SearchView.OnQueryTextListener,View.OnClickListener,ListView.OnItemClickListener{

    private ImageButton search_back_btn;
    private SearchView searchView;
    private ListView listView;
    private DBManager dbManager;
    private CityQuery cityQuery;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_city_layout);
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        cityQuery = new CityQuery(this);
        init();
    }

    public void init(){
        search_back_btn = (ImageButton) findViewById(R.id.btn_back);
        search_back_btn.setOnClickListener(this);
        searchView = (SearchView) findViewById(R.id.search_view);
        listView = (ListView) findViewById(R.id.auto_list_view);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setFocusable(true);
        searchView.setQueryHint("请输入要查询的城市");
        List<String> city = cityQuery.getCursor(dbManager.getDatabase());
        if (city.size() > 0){
            for (String cityName : city){
                dataList.add(cityName);
            }
            mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataList);
        }
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setTextFilterEnabled(true);
    }


    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                this.finish();
                break;
        }
    }


    @Override public boolean onClose() {
        if (searchView.getQuery().length() > 0){
            searchView.setQuery("",false);
        }else {
            this.finish();
        }
        return false;
    }


    @Override public boolean onQueryTextChange(String newText) {
        List<String> list = cityQuery.queryByLike(dbManager.getDatabase(),newText);
        if (list.size() > 0){
            dataList.clear();
            for (String city : list){
                dataList.add(city);
            }
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }


    @Override public boolean onQueryTextSubmit(String query) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("SelectCity",query);
        setResult(1,intent);
        this.finish();
        return true;
    }


    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedCity = (String)listView.getItemAtPosition(position);
        searchView.setQuery(selectedCity,true);
    }


    @Override protected void onDestroy() {
        dbManager.closeDatabase();
        super.onDestroy();
    }
}
