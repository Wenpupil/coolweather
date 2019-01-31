package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.db.ForecastBases;
import com.coolweather.android.db.Weathers;
import com.coolweather.android.util.HttpUtil;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.air.Air;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView apiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private static Weathers weathers;

    private ImageView bingPicImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //初始化和风sdk
        HeConfig.init("HE1901292113511074", "a02302bb41fc4e10933722c015f0615c");
        HeConfig.switchToFreeServerNode();

        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        apiText=(TextView)findViewById(R.id.api_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("xxss",null);
        if(weatherString!=null){
            weathers=DataSupport.findLast(Weathers.class);
            showWeatherInfo(weathers);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
       }

       String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(String weatherId){

        /*HeWeather.getAir(this, weatherId, Lang.CHINESE_SIMPLIFIED,
                Unit.METRIC, new HeWeather.OnResultAirBeanListener() {
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(List<Air> list) {
                        Air air=list.get(0);
                        weathers.setApi(air.getAir_now_city().getAqi());
                        weathers.setPm25(air.getAir_now_city().getPm25());
                        weathers.save();
                        showWeatherInfo(weathers);
                    }
                });*/

        HeWeather.getWeather(this, weatherId, Lang.CHINESE_SIMPLIFIED,
                Unit.METRIC, new HeWeather.OnResultWeatherDataListBeansListener() {
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(List<Weather> list) {
                        SharedPreferences.Editor editor=PreferenceManager.
                                getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("xxss",new Gson().toJson(list));
                        editor.apply();
                        final Weather tmp=list.get(0);
                        weathers=new Weathers();
                        weathers.setCityName(tmp.getBasic().getLocation());
                        weathers.setUpdateTime(tmp.getUpdate().getLoc().
                                substring(tmp.getUpdate().getLoc().length()-5));
                        weathers.setDegree(tmp.getNow().getTmp()+"℃");
                        weathers.setWeatherInfo(tmp.getNow().getCond_txt());

                        DataSupport.deleteAll(ForecastBases.class);
                        //weathers.setList(tmp.getDaily_forecast());
                        for(ForecastBase forecastBase:tmp.getDaily_forecast())
                        {
                            ForecastBases forecastBases=new ForecastBases();
                            forecastBases.setDate(forecastBase.getDate());
                            forecastBases.setCond(forecastBase.getCond_txt_d());
                            forecastBases.setMax(forecastBase.getTmp_max());
                            forecastBases.setMin(forecastBase.getTmp_min());
                            forecastBases.save();
                        }
                        weathers.setComfort(tmp.getLifestyle().get(0).getTxt());
                        weathers.setCarWash(tmp.getLifestyle().get(6).getTxt());
                        weathers.setSport(tmp.getLifestyle().get(3).getTxt());
                        weathers.setApi("63");
                        weathers.setPm25("28");
                        weathers.save();
                        showWeatherInfo(weathers);
                    }
                });
        loadBingPic();
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weathers weather){
        String cityName=weather.getCityName();
        String updateTime=weather.getUpdateTime();
        String degree=weather.getDegree();
        String weatherInfo=weather.getWeatherInfo();

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(ForecastBases forecastBases:DataSupport.findAll(ForecastBases.class)){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecastBases.getDate());
            infoText.setText(forecastBases.getCond());
            maxText.setText(forecastBases.getMax());
            minText.setText(forecastBases.getMin());
            forecastLayout.addView(view);
        }
        apiText.setText(weather.getApi());
        pm25Text.setText(weather.getPm25());
        String comfort="舒适度: "+weather.getComfort();
        String carWash="洗车指数： "+weather.getCarWash();
        String sport="运动建议： "+weather.getSport();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
