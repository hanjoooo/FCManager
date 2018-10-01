package com.example.khanj.fcmanager.Model;

/**
 * Created by khanj on 2018-09-23.
 */

public class DietRecord {
    private String date = "0";
    private Double pWeight = 0.0;
    private Double mWeight = 0.0;
    private int mCal=0;
    private int pCal =0;
    private int bmr = 0;
    private int exCal = 0;

    public DietRecord(){

    }

    public DietRecord(String data, Double pWeight,Double mWeight, int mCal, int pCal,int Bmr, int exCal){
        this.date=data;
        this.pWeight=pWeight;
        this.mWeight=mWeight;
        this.mCal=mCal;
        this.pCal=pCal;
        this.bmr = Bmr;
        this.exCal = exCal;
    }

    public String getDate() {
        return date;
    }
    public Double getmWeight() {
        return mWeight;
    }
    public Double getpWeight() {
        return pWeight;
    }
    public int getmCal() {
        return mCal;
    }
    public int getpCal() {
        return pCal;
    }

    public int getBmr() {
        return bmr;
    }

    public int getExCal() {
        return exCal;
    }
}

