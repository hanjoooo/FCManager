package com.example.khanj.fcmanager.Model;

/**
 * Created by khanj on 2018-10-06.
 */

public class FoodCalroie {
    private String fName = "음식이름";
    private int fCal=0;
    private int fCarbs=0;
    private int fFat =0;
    private int fProtiens = 0;
    private int fVitamin = 0;
    private int fMinerals = 0;

    public FoodCalroie(){

    }
    public FoodCalroie(String fname, int fcal, int fcarbs, int ffat,int fprotien,int fvitamin,int fminerals){
        this.fName=fname;
        this.fCal=fcal;
        this.fCarbs=fcarbs;
        this.fFat=ffat;
        this.fProtiens=fprotien;
        this.fVitamin=fvitamin;
        this.fMinerals=fminerals;
    }

    public String getfName() {
        return fName;
    }

    public int getfCal() {
        return fCal;
    }

    public int getfFat() {
        return fFat;
    }

    public int getfCarbs() {
        return fCarbs;
    }

    public int getfProtiens() {
        return fProtiens;
    }

    public int getfVitamin() {
        return fVitamin;
    }

    public int getfMinerals() {
        return fMinerals;
    }
}
