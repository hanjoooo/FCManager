package com.example.khanj.fcmanager.MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import jnr.ffi.annotations.In;

public class DietRecordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference childPWeightRef;
    private DatabaseReference childMWeightRef;
    private DatabaseReference childGenderRef;
    private DatabaseReference childAgeRef;
    private DatabaseReference childHeightRef;
    private DatabaseReference childStepRef;
    private DatabaseReference IntakeRef;
    private DatabaseReference childIntakeRef;

    private TextView txDate;
    private TextView txPweight;
    private TextView txMweight;
    private TextView txMkcal;
    private EditText etPkcal;
    private EditText exkcal;

    private String today = " ";
    private int rAge=0;
    private Double rHeight=0.0;
    private Double pWeight=0.0;
    private Double mWeight=0.0;
    private Double BMR=0.0;
    private int pCalorie = 0;
    private int mCalorie = 0;

    private Button btInsert;
    private Button btChange;
    private Button btCancle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_record);

        mAuth = FirebaseAuth.getInstance();
        txDate = (TextView)findViewById(R.id.date);
        txPweight = (TextView)findViewById(R.id.pweight);
        txMweight = (TextView)findViewById(R.id.mweight);
        txMkcal = (TextView)findViewById(R.id.mkcal);
        etPkcal = (EditText)findViewById(R.id.pkcal);
        exkcal = (EditText)findViewById(R.id.excal);
        btInsert = (Button)findViewById(R.id.insert);
        btCancle = (Button)findViewById(R.id.cancel);

        // 날짜는 현재 날짜로 고정
        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        txDate.setText(simpleDateFormat.format(date));
        today= simpleDateFormat.format(date);
        btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pCalorie = Integer.parseInt(etPkcal.getText().toString());
                int excal = Integer.parseInt(exkcal.getText().toString());
                DietRecord dietRecord = new DietRecord(today,pWeight,mWeight,mCalorie,pCalorie,BMR.intValue(),excal);
                childIntakeRef = IntakeRef.child(dietRecord.getDate());
                childIntakeRef.setValue(dietRecord);
                childStepRef.setValue(0);
                Toast.makeText(DietRecordActivity.this, "일지 등록 완료!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        childHeightRef = mchildRef.child("height");
        childAgeRef = mchildRef.child("age");
        childGenderRef = mchildRef.child("gender");
        childPWeightRef = mchildRef.child("Pweight");
        childMWeightRef = mchildRef.child("Mweight");
        IntakeRef = mchildRef.child("DietRecord");
        childStepRef = mchildRef.child("curStep");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        IntakeRef.child(simpleDateFormat.format(date)).child("pCal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    etPkcal.setText(""+dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        childAgeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String age = dataSnapshot.getValue(String.class);
                rAge = Integer.parseInt(age);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        childHeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String height = dataSnapshot.getValue(String.class);
                rHeight = Double.parseDouble(height);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        childMWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Mweight = dataSnapshot.getValue(String.class);
                txMweight.setText(Mweight+"kg");
                mWeight = Double.parseDouble(Mweight);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        childPWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Pweight= dataSnapshot.getValue(String.class);
                txPweight.setText(Pweight+"kg");
                pWeight = Double.parseDouble(Pweight);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        childStepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int val = Integer.parseInt(dataSnapshot.getValue().toString());
                Double stepCal = val*0.03;
                exkcal.setText(""+stepCal.intValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        childGenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender = dataSnapshot.getValue(String.class);
                if(gender.equals("남자")){
                    BMR =  ((13.7*(pWeight*0.8))+(5*rHeight)-(6.8*rAge))*1.375;
                    Double changeweight = BMR*((mWeight - pWeight)/90);
                    mCalorie = BMR.intValue()+changeweight.intValue();
                    txMkcal.setText(mCalorie+" kcal");
                }
                else{
                    BMR =  ((9.48*(pWeight*0.8))+(1.85*rHeight)-(4.7*rAge)+655)*1.2;
                    Double changeweight = BMR*((mWeight - pWeight)/90);
                    mCalorie = BMR.intValue()+changeweight.intValue();
                    txMkcal.setText(mCalorie+" kcal");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
