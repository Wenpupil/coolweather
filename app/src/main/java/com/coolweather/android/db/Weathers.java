package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Weathers extends DataSupport {

    private int id;

    private String cityName;

    private String updateTime;

    private String degree;

    private String weatherInfo;

    private String api;

    private String pm25;

    private String comfort;

    private String carWash;

    private String sport;

    private List<ForecastBases> list=new ArrayList<>();

    public void setId(int id){
        this.id=id;
    }

    public void setCityName(String cityName){
        this.cityName=cityName;
    }

    public void setUpdateTime(String updateTime){
        this.updateTime=updateTime;
    }

    public void setDegree(String degree){
        this.degree=degree;
    }

    public void setWeatherInfo(String weatherInfo){
        this.weatherInfo=weatherInfo;
    }

    public void setApi(String api){
        this.api=api;
    }

    public void setPm25(String pm25){
        this.pm25=pm25;
    }

    public void setComfort(String comfort){
        this.comfort=comfort;
    }

    public void setCarWash(String carWash){
        this.carWash=carWash;
    }

    public void setSport(String sport){
        this.sport=sport;
    }

    public void setList(List<ForecastBases> list){
        this.list=list;
    }

    public int getId(){
        return id;
    }

    public String getCityName(){
        return cityName;
    }

    public String getUpdateTime(){
        return updateTime;
    }

    public String getDegree(){
        return degree;
    }

    public String getWeatherInfo(){
        return weatherInfo;
    }

    public String getApi(){
        return api;
    }

    public String getPm25(){
        return pm25;
    }

    public String getComfort(){
        return comfort;
    }

    public String getCarWash(){
        return carWash;
    }

    public String getSport(){
        return sport;
    }

    public List<ForecastBases> getList(){
        return list;
    }
}
