package com.example.khanj.fcmanager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.khanj.fcmanager.Base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegistActivity extends BaseActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private EditText edname;
    private EditText edAge;
    private EditText edHeight;
    private EditText edPWeight;
    private EditText edMWeight;
    private RadioGroup rdGender;
    private String edGender="";

    private Button btnDone;
    private Button btnCancel;
    private FirebaseAuth mAuth;

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference childIDRef;
    private DatabaseReference childPasswordRef;
    private DatabaseReference childNameRef;
    private DatabaseReference childGenderRef;
    private DatabaseReference childAgeRef;
    private DatabaseReference childPWeightRef;
    private DatabaseReference childMWeightRef;
    private DatabaseReference childHeightRef;
    private DatabaseReference childStepRef;



    private static final String TAG = "EmailPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        edname = (EditText) findViewById(R.id.edname);
        edAge = (EditText) findViewById(R.id.edage);
        edHeight = (EditText) findViewById(R.id.edheight);
        edPWeight = (EditText)findViewById(R.id.edpweight);
        edMWeight = (EditText)findViewById(R.id.edmweight);
        rdGender = (RadioGroup)findViewById(R.id.rdgender);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        mAuth = FirebaseAuth.getInstance();


        // 비밀번호 일치 검사
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = etPasswordConfirm.getText().toString();

                if (password.equals(confirm)) {
                    etPassword.setBackgroundColor(Color.GREEN);
                    etPasswordConfirm.setBackgroundColor(Color.GREEN);
                } else {
                    etPassword.setBackgroundColor(Color.RED);
                    etPasswordConfirm.setBackgroundColor(Color.RED);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        rdGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                edGender=((RadioButton)findViewById(checkedId)).getText().toString();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이메일 입력 확인
                if (etEmail.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "이메일(아이디) 입력하세요!", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if (etPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if (etPasswordConfirm.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호 확인을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPasswordConfirm.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                    Toast.makeText(RegistActivity.this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }
                // 이름 입력 확인
                if (edname.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                    edname.requestFocus();
                    return;
                }
                // 나이 입력 확인
                if (edAge.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "나이를 입력하세요!", Toast.LENGTH_SHORT).show();
                    edAge.requestFocus();
                    return;
                }
                // 키 입력 확인
                if (edHeight.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "키를 입력하세요!", Toast.LENGTH_SHORT).show();
                    edHeight.requestFocus();
                    return;
                }
                // 현재몸구게 입력 확인
                if (edPWeight.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "현재몸무게를 입력하세요!", Toast.LENGTH_SHORT).show();
                    edPWeight.requestFocus();
                    return;
                }
                // 목표몸무게 입력 확인
                if (edMWeight.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "목표몸무게를 입력하세요!", Toast.LENGTH_SHORT).show();
                    edMWeight.requestFocus();
                    return;
                }

                createAccount(etEmail.getText().toString(), etPassword.getText().toString());

                // [END create_user_with_email]
                // 자신을 호출한 Activity로 데이터를 보낸다.

            }
        });

    }


    public void printChecked(View v, int position) {
    }
    protected void onStart() {
        super.onStart();
        final int[] can = {1};
        mAuth.addAuthStateListener(mAuthListener);


    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }// [START create_user_with_email]
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistActivity.this, "회원가입 실패",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistActivity.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                            childIDRef.setValue(etEmail.getText().toString());
                            childPasswordRef.setValue(etPassword.getText().toString());
                            childNameRef.setValue(edname.getText().toString());
                            childGenderRef.setValue(edGender);
                            childAgeRef.setValue(edAge.getText().toString());
                            childHeightRef.setValue(edHeight.getText().toString());
                            childPWeightRef.setValue(edPWeight.getText().toString());
                            childMWeightRef.setValue(edMWeight.getText().toString());
                            childStepRef.setValue(0);
                            signOut();
                            finish();
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }


    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mchildRef = mConditionRef.child(user.getUid());
            childIDRef = mchildRef.child("email");
            childPasswordRef = mchildRef.child("passward");
            childNameRef=mchildRef.child("name");
            childGenderRef=mchildRef.child("gender");
            childAgeRef=mchildRef.child("age");
            childHeightRef=mchildRef.child("height");
            childPWeightRef=mchildRef.child("Pweight");
            childMWeightRef=mchildRef.child("Mweight");
            childStepRef = mchildRef.child("curStep");

        } else {

        }
    }
    private void signOut() {
        showProgressDialog();
        mAuth.signOut();
        hideProgressDialog();
    }
}