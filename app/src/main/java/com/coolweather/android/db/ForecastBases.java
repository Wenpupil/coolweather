package com.coolweather.android.db;

import org.litepal.crud.DataSupport;
public class ForecastBases  extends DataSupport {

    private String date;

    private String cond;

    private String max;

    private String min;

    public void setDate(String date){
        this.date=date;
    }

    public void setCond(String cond){
        this.cond=cond;
    }

    public void setMax(String max){
        this.max=max;
    }

    public void setMin(String min){
        this.min=min;
    }

    public String getDate(){
        return date;
    }

    public String getCond(){
        return cond;
    }

    public String getMax(){
        return max;
    }

    public String getMin(){
        return min;
    }
}
