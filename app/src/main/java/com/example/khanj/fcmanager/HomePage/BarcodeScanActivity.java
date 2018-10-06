package com.example.khanj.fcmanager.HomePage;

import android.app.Activity;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.example.khanj.fcmanager.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class BarcodeScanActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        textViewResult = (TextView)  findViewById(R.id.textViewResult);
        IntentIntegrator qrScan = new IntentIntegrator(this);

        qrScan.setPrompt("음식 스캔중...");
        qrScan.setOrientationLocked(true);
        qrScan.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(BarcodeScanActivity.this, "스캔 취소!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(BarcodeScanActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BarcodeScanActivity.this.getApplicationContext(),HomeFragment.class);
                intent.putExtra("data",result.getContents());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
