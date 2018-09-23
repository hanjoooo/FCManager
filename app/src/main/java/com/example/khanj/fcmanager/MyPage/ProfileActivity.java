package com.example.khanj.fcmanager.MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.RegistActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference childIDRef;
    private DatabaseReference childNameRef;
    private DatabaseReference childGenderRef;
    private DatabaseReference childAgeRef;
    private DatabaseReference childPWeightRef;
    private DatabaseReference childMWeightRef;
    private DatabaseReference childHeightRef;

    TextView txEmail;
    TextView txName;
    TextView txGender;
    EditText etAge;
    EditText etHeight;
    EditText etPWeight;
    EditText etMWeight;

    private Button btnDone;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        txEmail = (TextView)findViewById(R.id.txemail);
        txName = (TextView)findViewById(R.id.txname);
        txGender = (TextView)findViewById(R.id.txgender);
        etAge = (EditText)findViewById(R.id.etage);
        etHeight= (EditText)findViewById(R.id.etheight);
        etPWeight = (EditText)findViewById(R.id.etpweight);
        etMWeight = (EditText)findViewById(R.id.etmweight);

        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 나이 입력 확인
                if (etAge.getText().toString().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "나이를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etAge.requestFocus();
                    return;
                }
                // 키 입력 확인
                if (etHeight.getText().toString().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "키를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etHeight.requestFocus();
                    return;
                }
                // 현재몸구게 입력 확인
                if (etPWeight.getText().toString().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "현재몸무게를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPWeight.requestFocus();
                    return;
                }
                // 목표몸무게 입력 확인
                if (etMWeight.getText().toString().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "목표몸무게를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etMWeight.requestFocus();
                    return;
                }
                childAgeRef.setValue(etAge.getText().toString());
                childHeightRef.setValue(etHeight.getText().toString());
                childPWeightRef.setValue(etPWeight.getText().toString());
                childMWeightRef.setValue(etMWeight.getText().toString());
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        childIDRef = mchildRef.child("email");
        childNameRef = mchildRef.child("name");
        childGenderRef = mchildRef.child("gender");
        childAgeRef = mchildRef.child("age");
        childHeightRef = mchildRef.child("height");
        childPWeightRef = mchildRef.child("Pweight");
        childMWeightRef = mchildRef.child("Mweight");


        childIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                txEmail.setText(id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                txName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childGenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gender = dataSnapshot.getValue(String.class);
                txGender.setText(gender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childAgeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String age = dataSnapshot.getValue(String.class);
                etAge.setText(age);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childHeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String height = dataSnapshot.getValue(String.class);
                etHeight.setText(height);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childPWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String weight = dataSnapshot.getValue(String.class);
                etPWeight.setText(weight);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childMWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String weight = dataSnapshot.getValue(String.class);
                etMWeight.setText(weight);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
