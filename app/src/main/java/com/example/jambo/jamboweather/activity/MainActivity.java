package com.example.jambo.jamboweather.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.jambo.jamboweather.R;
import com.example.jambo.jamboweather.adapter.MainActivityPagerAdapter;
import com.example.jambo.jamboweather.adapter.WeatherAdapter;
import com.example.jambo.jamboweather.db.SelectedCityDBManager;
import com.example.jambo.jamboweather.mould.Weather;
import com.example.jambo.jamboweather.util.ACache;
import com.example.jambo.jamboweather.util.HttpUtil;
import com.example.jambo.jamboweather.util.WeatherList;
import java.util.ArrayList;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AMapLocationListener,NavigationView.OnNavigationItemSelectedListener{
    private ViewPager mViewPager;
    private NavigationView mNavigationView;
    private MainActivityPagerAdapter mainActivityPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mFatherLinearLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView mCityTV;
    private TextView mTimeTV;
    private Button mAddCityBtn;
    private Observer<Weather> observer;
    private ACache mACache;
    private WeatherAdapter mWeatherAdapter;
    private static final String TAG = "MainActivity";
    private final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private SharedPreferences mPreference;
    private String location_city;
    private SelectedCityDBManager selectedCityDBManager;
    private static final int LOCATION_REQUEST_CODE = 007;

    /**
     * 先从数据库查询城市，若为空默认加载北京的天气，同时请求定位权限
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewId();
        mACache = ACache.get(this);
        selectedCityDBManager = new SelectedCityDBManager(this);
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        location_city = mPreference.getString("location_city","");
        requestLocationPermission();
        loadCity();
    }


    /**
     * 初始化控件
     */
    public void findViewId(){
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mNavigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        mNavigationView.setNavigationItemSelectedListener(this);
        mainActivityPagerAdapter = new MainActivityPagerAdapter();
        mViewPager.setAdapter(mainActivityPagerAdapter);
    }


    /**
     * 根据城市名在ViewPager中添加对应的View，同时将数据存入数据库中
     * @param city_name
     * @param city_id
     */
    private void initView(final String city_name, int city_id){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_show_weather,null);
        mToolbar = (Toolbar) view.findViewById(R.id.header_toolbar);
        mCityTV = (TextView) view.findViewById(R.id.header_city);
        mTimeTV = (TextView) view.findViewById(R.id.header_time);
        mAddCityBtn = (Button) view.findViewById(R.id.header_add_city);
        mAddCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchCityActivity.class);
                startActivityForResult(intent,1);
            }
        });
        ActionBarDrawerToggle mActionbar = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open,R.string.close);
        mDrawerLayout.setDrawerListener(mActionbar);
        mActionbar.syncState();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if (!city_name.trim().toString().equals(location_city)){
            mNavigationView.getMenu()
                .add(R.id.city_group,city_id, Menu.NONE,city_name)
                .setIcon(R.drawable.place)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        closeDrawerLayout();
                        setCurrentPager(mainActivityPagerAdapter.getViewForTag(city_name));
                        return true;
                    }
                });
        }
        view.setTag(city_name);
        addView(view);
        mainActivityPagerAdapter.notifyDataSetChanged();
        selectedCityDBManager.addCity(city_name,city_id);
    }


    /**
     * 侧滑界面菜单
     * @param item
     * @return
     */
    @Override public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.city_manager:
                closeDrawerLayout();
                startActivityForResult(new Intent(MainActivity.this,CityManagerActivity.class),2);
                break;
            case R.id.city_location:
                closeDrawerLayout();
                judgeSelectCity(location_city);
                break;
            case R.id.about:
                closeDrawerLayout();
                Toast.makeText(MainActivity.this,"about?",Toast.LENGTH_LONG).show();
                break;
            case R.id.activity_settings:
                closeDrawerLayout();
                startActivityForResult(new Intent(MainActivity.this,SearchCityActivity.class),1);
                break;
            default:
                closeDrawerLayout();
        }
        return true;
    }


    /**
     * 判断当前系统版本，6.0以上则尝试请求定位权限，此处仅针对我的5x做的测试
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
        }else {
            location_city = mPreference.getString("location_city","");
            Toast.makeText(this,"已获取定位权限",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 请求权限后的操作，若同意则开始定位
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    location();
                    Toast.makeText(MainActivity.this,"you get location permission",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"not get location permission",Toast.LENGTH_SHORT).show();
                }
        }

    }


    /**
     * 根据城市名去加载城市数据并加载界面，若无数据则先从本地去查找添加
     * @param city_name
     */
    public void queryData(final String city_name) {
        observer = new Observer<Weather>() {
            @Override public void onCompleted() {
                Log.d(TAG,"onCompleted");
            }
            @Override public void onError(Throwable e) {
                Log.e(TAG,e.getMessage().toString());
            }
            @Override public void onNext(Weather weather) {
                int city_id = Integer.parseInt(weather.basic.id.replace("CN", ""));
                mACache.put(city_name, weather);
                initView(city_name, city_id);
                mCityTV.setText(weather.basic.city);
                mTimeTV.setText(convertTime(weather.basic.update.loc));
                mWeatherAdapter = new WeatherAdapter(MainActivity.this, weather);
                mRecyclerView.setAdapter(mWeatherAdapter);
            }
        };
        queryWeatherDataForACache(observer,city_name);
    }


    /**
     * 从本地查找加载数据，若有则传入数据，否则去服务器加载数据
     * @param observer
     * @param city_name
     */
    public void queryWeatherDataForACache(Observer<Weather> observer, String city_name){
        Weather weather = null;
        try{
            weather = (Weather) mACache.getAsObject(city_name);

        }catch (Exception e) {
            Log.e(TAG,e.getMessage().toString());
        }
        if ( weather != null){
            Observable.just(weather).distinct().subscribe(observer);
        }else {
            queryWeatherDataForService(observer,city_name);
        }
    }


    /**
     * 使用Retrofit从服务器加载数据
     * @param observer
     * @param city_name
     */
    public void queryWeatherDataForService(Observer<Weather> observer, final String city_name){
        HttpUtil.getmWeatherApi().getWeather(city_name,KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Func1<WeatherList, Weather>() {
                @Override public Weather call(WeatherList weatherList) {
                    return weatherList.mWeathers.get(0);
                }
            })
            .doOnNext(new Action1<Weather>() {
                @Override public void call(Weather weather) {
                    mACache.put(city_name,weather);
                }
            })
            .subscribe(observer);
    }


    private int addView(View defuntView){
        int pagerIndex = mainActivityPagerAdapter.addView(defuntView);
        mainActivityPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pagerIndex);
        return  pagerIndex;
    }

    public void setCurrentPager(View showView){
        mViewPager.setCurrentItem(mainActivityPagerAdapter.getItemPosition(showView),true);
    }


    private void removeView(View defuntPage){
        int pagerIndex = mainActivityPagerAdapter.removeView(mViewPager,defuntPage);
        if (pagerIndex == mainActivityPagerAdapter.getCount()){
            pagerIndex--;
        }
        mViewPager.setCurrentItem(pagerIndex);
    }

    public String convertTime(String time){
        String [] times = time.split(" ");
        return times[1];
    }


    public boolean isDrawerLalose(){
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


    public void closeDrawerLayout(){
        if (isDrawerLalose()){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        mLocationOption.setInterval(24 * 3600 * 1000);
        //给定位客户端对象设置定位参 数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //aMapLocation.getLatitude();//获取纬度
                //aMapLocation.getLongitude();//获取经度
                //aMapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Date date = new Date(aMapLocation.getTime());
                //df.format(date);//定位时间
                //aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                //aMapLocation.getCountry();//国家信息
                //aMapLocation.getProvince();//省信息
                //aMapLocation.getCity();//城市信息
                //aMapLocation.getDistrict();//城区信息
                //aMapLocation.getStreet();//街道信息
                //aMapLocation.getStreetNum();//街道门牌号信息
                //aMapLocation.getCityCode();//城市编码
                //aMapLocation.getAdCode();//地区编码
                location_city = aMapLocation.getCity().replace("市", "")
                    .replace("省", "")
                    .replace("土家族苗族自治州", "")
                    .replace("自治区", "")
                    .replace("特别行政区", "")
                    .replace("地区", "")
                    .replace("盟", "");
                Log.d(TAG,location_city+"location");
                Log.d(TAG,"begin location");
                mPreference.edit().putString("location_city",location_city).apply();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                    aMapLocation.getErrorInfo());
            }
        }
    }


    private void loadCity(){
        Cursor cursor  = selectedCityDBManager.selectAllCity();
        if (cursor.moveToFirst()){
            do {
                String city_name = cursor.getString(cursor.getColumnIndex("name")).trim();
                Log.d(TAG,city_name);
                queryData(city_name);
            }while (cursor.moveToNext());
        }else {
            queryData("北京");
            cursor.close();
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String SelectCity = data.getStringExtra("SelectCity");
                    judgeSelectCity(SelectCity);
                } else {
                    return;
                }
                break;
            case 2:
                if (data != null) {
                    ArrayList<String> removeCities = data.getStringArrayListExtra("removeCities");
                    for (String name : removeCities) {
                        int removeItemId = selectedCityDBManager.queryIdForName(name);
                        removeView(mainActivityPagerAdapter.getViewForTag(name));
                        mNavigationView.getMenu().removeItem(removeItemId);
                        selectedCityDBManager.deleteCity(name);
                    }
                } else {
                    return;
                }
                break;
        }
    }

    private void judgeSelectCity(String SelectCity){
        if (!selectedCityDBManager.isExisted(SelectCity)) {
            queryData(SelectCity);
        } else {
            setCurrentPager(mainActivityPagerAdapter.getViewForTag(SelectCity));
        }
    }

}
