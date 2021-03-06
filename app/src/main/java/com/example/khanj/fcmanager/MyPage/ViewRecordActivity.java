package com.example.khanj.fcmanager.MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.adapter.RecordListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference IntakeRef;

    private RecyclerView recyclerView;
    private ArrayList<DietRecord> mItems = new ArrayList<>();
    private ArrayList<DietRecord>tmp = new ArrayList<>();
    private RecordListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.view7).setOnClickListener(this);
        findViewById(R.id.view30).setOnClickListener(this);
        findViewById(R.id.view90).setOnClickListener(this);
        //adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,LIST_MENU);
        adapter = new RecordListAdapter(mItems);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        IntakeRef = mchildRef.child("DietRecord");
        IntakeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mItems.clear();
                tmp.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    DietRecord record = data.getValue(DietRecord.class);
                    tmp.add(record);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        //선택하게끔 하자 !!!!
        if(v.getId()==R.id.view7){
            if(tmp.size()>0){
                int count =7;
                mItems.clear();
                for (int i = tmp.size()-1; i >=0; i--) {
                    if(count==0){
                        break;
                    }
                    mItems.add(tmp.get(i));
                    count--;
                }
                adapter.notifyDataSetChanged();
            }


        }
        else if(v.getId()==R.id.view30){
            if(tmp.size()>0){
                int count =30;
                mItems.clear();
                for (int i = tmp.size()-1; i >=0; i--) {
                    if(count==0){
                        break;
                    }
                    mItems.add(tmp.get(i));
                    count--;
                }
                adapter.notifyDataSetChanged();
            }

        }
        else if(v.getId()==R.id.view90){
            if(tmp.size()>0){
                int count =30;
                mItems.clear();
                for (int i = tmp.size()-1; i >=0; i--) {
                    if(count==0){
                        break;
                    }
                    mItems.add(tmp.get(i));
                    count--;
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
