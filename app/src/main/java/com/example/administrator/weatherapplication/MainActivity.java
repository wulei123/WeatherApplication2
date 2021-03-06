package com.example.administrator.weatherapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.example.administrator.weatherapplication.adapter.ViewPagerAdapter;
import com.example.administrator.weatherapplication.adapter.WeatherView;
import com.example.administrator.weatherapplication.database.SQLite;
import com.example.administrator.weatherapplication.weather.GetCityName;
import com.example.administrator.weatherapplication.weather.GetWeather;
import com.example.administrator.weatherapplication.weather.Weather;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


//    private TextView showData;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String cityNameFromNetWork = null;
    private Weather weather=null;
    private boolean hasPositioned = false;
    private SQLite sqLite;
    private SQLiteOpenHelper sqHelper;
    private ViewPager weatherPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ArrayList<View> list;
//    private String[] cities={"北京","青岛","黄岛","衡水","北京"};

    List<String> cities = new ArrayList<String>();





    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                    getNetWorkPosition();
                return;
        }
    }
    private void getNetWorkPosition(){
//                 locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }
    public class GetCityTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... params) {
            String httpUrl = "http://apis.baidu.com/thinkpage/weather_api/suggestion";
            String httpArg = "location="+params[0]+"&language=zh-Hans&unit=c&start=0&days=3";
            cityNameFromNetWork = new GetCityName().request(httpUrl, httpArg);
            return cityNameFromNetWork;
        }


        @Override
        protected void onPostExecute(String cityName) {
            cityNameFromNetWork= cityName;
            setUpPosition();
        }
    }
    protected void setUpPosition(){
        if(cityNameFromNetWork!=null){


            new GetWeatherTask().execute(cityNameFromNetWork);
                }


                for (String city :
                        cities) {
                    new GetWeatherTask().execute(city);
                }


        }

    public class GetWeatherTask extends AsyncTask<String,Void,Weather>{
        @Override
        protected void onPreExecute() {}

        @Override
        protected Weather doInBackground(String... params) {
            String httpUrl = "http://apis.baidu.com/heweather/weather/free";
            String httpArg = "city="+params[0];
            weather = new GetWeather().request(httpUrl, httpArg);
            return weather;
        }


        @Override
        protected void onPostExecute(Weather weatherData) {
            weather = weatherData;
            setUpWeather();
        }

    }
    protected void setUpWeather(){
        if(weather!=null){
//            showData.append("weather:"+weather.results.get(0).status+"\n");
//            showData.append("weather:"+weather.results.get(0).basic.city+"\n");
            list.add(new WeatherView(weather,MainActivity.this).getView());
            viewPagerAdapter.notifyDataSetChanged();
            String s = weather.results.get(0).status;
            Log.e("status:",s);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);



        SQLite db;
        db = new SQLite(this,"WEATHER",null,7);
        db.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM WEATHER",null);
        if(cursor.moveToFirst()){
            do{
                String ci = cursor.getString(cursor.getColumnIndex("CITY"));
                cities.add(ci);
//                wind = cursor.getString(cursor.getColumnIndex("WIND"));
            }while (cursor.moveToNext());
        }
        cursor.close();










        weatherPager = (ViewPager)findViewById(R.id.weather_pager);
        list = new ArrayList<View>();
        viewPagerAdapter=new ViewPagerAdapter(this,list);
        weatherPager.setAdapter(viewPagerAdapter);



















        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//       showData = (TextView)findViewById(R.id.show_data);






        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(hasPositioned==false){
                    new GetCityTask().execute(new Double(location.getLatitude()).toString()+":"+new Double(location.getLongitude()));
                    hasPositioned=true;
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                /*Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);*/
            }
        };








        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                },10);
                return;
            }
        }else{
            getNetWorkPosition();
        }















    }





















    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this,ChoseCityActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this,CityList.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }











}
