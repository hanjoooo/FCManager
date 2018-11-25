package com.example.khanj.fcmanager.Model;

/**
 * Created by khanj on 2018-10-06.
 */

public class FoodCalroie {
    private String fName = "음식이름";
    private int fCal=0;
    private double fCarbs=0;
    private double fFat =0;
    private double fProtiens = 0;
    private double fNa = 0;
    private int fWeight= 0;
    private String imguri= " ";

    public FoodCalroie(){

    }
    public FoodCalroie(String fname, int fcal, double fcarbs, double ffat,double fprotien,double fNa,int fWeight,String imguri){
        this.fName=fname;
        this.fCal=fcal;
        this.fCarbs=fcarbs;
        this.fFat=ffat;
        this.fProtiens=fprotien;
        this.fNa=fNa;
        this.fWeight=fWeight;
        this.imguri = imguri;
    }

    public String getfName() {
        return fName;
    }

    public int getfCal() {
        return fCal;
    }

    public double getfFat() {
        return fFat;
    }

    public double getfCarbs() {
        return fCarbs;
    }

    public double getfProtiens() {
        return fProtiens;
    }

    public double getfNa() {
        return fNa;
    }

    public int getfWeight() {
        return fWeight;
    }

    public String getImguri() {
        return imguri;
    }

}
