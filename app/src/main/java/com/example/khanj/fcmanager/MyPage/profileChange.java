package com.example.khanj.fcmanager.MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.khanj.fcmanager.Helper.DBHelper;
import com.example.khanj.fcmanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class profileChange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);

        // 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);

        final TextView txDate = (TextView) findViewById(R.id.date);
        final EditText etWeight = (EditText) findViewById(R.id.weight);
        final EditText etKcal = (EditText) findViewById(R.id.kcal);

        // 날짜는 현재 날짜로 고정
        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        txDate.setText(simpleDateFormat.format(date));

        // DB에 데이터 추가
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = txDate.getText().toString();
                int weight = Integer.parseInt(etWeight.getText().toString());
                int kcal = Integer.parseInt(etKcal.getText().toString());

                dbHelper.insert(date,weight,kcal);
                result.setText(dbHelper.getResult());
            }
        });

        // DB에 있는 데이터 수정
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int weight =  Integer.parseInt(etWeight.getText().toString());
                int kcal = Integer.parseInt(etKcal.getText().toString());

                dbHelper.update(weight, kcal);
                result.setText(dbHelper.getResult());
            }
        });

        // DB에 있는 데이터 삭제
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = txDate.getText().toString();
                dbHelper.delete(date);
            }
        });

        // DB에 있는 데이터 조회
        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(dbHelper.getResult());
            }
        });
    }
}


