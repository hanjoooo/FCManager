package com.example.khanj.fcmanager.HomePage;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khanj.fcmanager.Model.FoodCalroie;
import com.example.khanj.fcmanager.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class FoodDataActivity extends AppCompatActivity {

    private FoodCalroie foodData = new FoodCalroie();
    private ImageView ivFoodImg;
    private TextView txFoodName;
    private TextView txFoodCal;
    private TextView txFoodWeight;
    private TextView txFoodCarbs;
    private TextView txFoodFat;
    private TextView txFoodProtein;
    private TextView txFoodNa;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_data);
        ivFoodImg = (ImageView)findViewById(R.id.foodimg);
        txFoodCal = (TextView)findViewById(R.id.foodcal);
        txFoodName = findViewById(R.id.txfoodname);
        txFoodWeight = findViewById(R.id.foodweight);
        txFoodCarbs = findViewById(R.id.fcarb);
        txFoodFat = findViewById(R.id.ffat);
        txFoodProtein = findViewById(R.id.fpro);
        txFoodNa = findViewById(R.id.fna);
        pieChart = findViewById(R.id.piechart);

        foodData = (FoodCalroie) getIntent().getSerializableExtra("fooddata");

        if(foodData.getImguri().equals(" ")){
            ivFoodImg.setImageResource(R.drawable.ic_launcher_icon);
        }else{
            ivFoodImg.setImageURI(Uri.parse(foodData.getImguri()));

        }
        txFoodName.setText(foodData.getfName().toString());
        txFoodCal.setText(foodData.getfCal()+" kcal");
        txFoodWeight.setText(foodData.getfWeight()+" g");
        txFoodCarbs.setText(foodData.getfCarbs()+" g");
        txFoodProtein.setText(foodData.getfProtiens()+" g");
        txFoodFat.setText(foodData.getfFat()+" g");
        txFoodNa.setText(foodData.getfNa()+" mg");


        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(70f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry((int)(foodData.getfCarbs()*100),"탄수화물"));
        yValues.add(new PieEntry((int)(foodData.getfProtiens()*100),"단백질"));
        yValues.add(new PieEntry((int)(foodData.getfFat()*100),"지방"));



        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);;

        pieChart.setData(data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
