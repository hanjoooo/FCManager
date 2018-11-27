package com.example.khanj.fcmanager.MyPage;

import android.content.Intent;
import android.content.Loader;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.khanj.fcmanager.Base.BaseFragment;
import com.example.khanj.fcmanager.LoginActivity;
import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.Model.FoodCalroie;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.event.ActivityResultEvent;
import com.example.khanj.fcmanager.loading.LoadingFragment;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */

public class MyPageFragment extends LoadingFragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mFoodRef = mRootRef.child("food");
    private DatabaseReference mchildRef;
    private DatabaseReference childNameRef;
    private DatabaseReference childGenderRef;
    private DatabaseReference childAgeRef;
    private DatabaseReference childHeightRef;
    private DatabaseReference childPWeightRef;
    private DatabaseReference childMWeightRef;
    private DatabaseReference IntakeRef;
    private DatabaseReference childFoodRef;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference schildRef;
    private StorageReference sprofileRef;


    TextView txName;
    TextView txPWeight;
    TextView txMWeight;
    TextView txBMR;
    TextView txCalorie;

    ImageView profileImg;

    private LineChart lineChart;
    private PieChart pieChart;

    static int REQUEST_PHOTO_ALBUM=1;
    private Realm mRealm;
    private int rAge=0;
    private Double rHeight=0.0;
    private Double rPweight=0.0;
    private Double rMweight=0.0;
    private int rCalorie = 0;

    private Bitmap bitmap;

    static final String[] LIST_MENU = {"내 정보 수정","프로필 사진 수정","암호 변경","로그아웃","계정 삭제"};
    static final String[] LIST2_MENU = {"일지 기록하기","일지 기록보기","체중 변화보기","영양 정보보기","일일필요열랑 계산"};
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page, container, false);
        mRealm = Realm.getDefaultInstance();

        mAuth = FirebaseAuth.getInstance();

        txName = (TextView)v.findViewById(R.id.txname);
        txPWeight = (TextView)v.findViewById(R.id.txpweight);
        txMWeight = (TextView)v.findViewById(R.id.txmweight);
        txCalorie = (TextView)v.findViewById(R.id.txcalorie);
        txBMR = (TextView)v.findViewById(R.id.txbmr);
        profileImg = (ImageView)v.findViewById(R.id.profileimg);
        //lineChart = (LineChart)v.findViewById(R.id.chart);
        pieChart = (PieChart)v.findViewById(R.id.piechart);

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),android.R.layout.simple_list_item_1,LIST_MENU);
        ArrayAdapter adapter2 = new ArrayAdapter(this.getActivity(),android.R.layout.simple_list_item_1,LIST2_MENU);
        ListView listView = (ListView)v.findViewById(R.id.listview);
        ListView listView2 = (ListView)v.findViewById(R.id.listview2);

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        setListViewHeightBasedOnChildren(listView);
        setListViewHeightBasedOnChildren(listView2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getContext(),ProfileActivity.class);
                    startActivity(intent);
                }
                else if(position==1){
                    photoAlbum();
                }
                else if(position == 3){
                    signOut();
                }
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(getContext(),DietRecordActivity.class);
                    startActivity(intent);
                }
                else if(position==1){
                    Intent intent = new Intent(getContext(),ViewRecordActivity.class);
                    startActivity(intent);
                }
                else if(position==3){
                    Intent intent = new Intent(getContext(),FoodCalorieCrawlingActivity.class);
                    startActivity(intent);
                }
                else if(position==4){
                    Intent intent = new Intent(getContext(),BmrCrawlingActivity.class);
                    startActivity(intent);
                }
            }
        });

       profileImg.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               photoAlbum();
               return false;
           }
       });

        return v;
        //
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        schildRef = storageRef.child(currentUser.getUid());
        childFoodRef = mFoodRef.child(currentUser.getUid());
        childNameRef = mchildRef.child("name");
        childHeightRef = mchildRef.child("height");
        childAgeRef = mchildRef.child("age");
        childGenderRef = mchildRef.child("gender");
        childPWeightRef = mchildRef.child("Pweight");
        childMWeightRef = mchildRef.child("Mweight");
        sprofileRef = schildRef.child("profileImg");
        IntakeRef = mchildRef.child("DietRecord");

        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

        childFoodRef.child(simpleDateFormat.format(date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    double carbs = 0.0;
                    double fat = 0.0;
                    double na = 0.0;
                    double protein =0.0;
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        carbs+=data.getValue(FoodCalroie.class).getfCarbs();
                        fat+=data.getValue(FoodCalroie.class).getfFat();
                        na+=data.getValue(FoodCalroie.class).getfNa();
                        protein+=data.getValue(FoodCalroie.class).getfProtiens();
                    }

                    pieChart.setUsePercentValues(true);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setExtraOffsets(5,10,5,5);

                    pieChart.setDragDecelerationFrictionCoef(0.95f);

                    pieChart.setDrawHoleEnabled(false);
                    pieChart.setHoleColor(Color.WHITE);
                    pieChart.setTransparentCircleRadius(61f);

                    ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

                    yValues.add(new PieEntry((int)(protein*10000),"단백질"));
                    yValues.add(new PieEntry((int)(carbs*10000),"탄수화물"));
                    yValues.add(new PieEntry((int)(fat*10000),"지방"));
                    yValues.add(new PieEntry((int)(na*10),"나트륨"));
                    Description descriptions = new Description();
                    descriptions.setText(""); //라벨
                    descriptions.setTextSize(15);
                    pieChart.setDescription(descriptions);

                    pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

                    PieDataSet dataSet = new PieDataSet(yValues,"");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                    PieData data = new PieData((dataSet));
                    data.setValueTextSize(10f);
                    data.setValueTextColor(Color.YELLOW);;

                    pieChart.setData(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        IntakeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ArrayList<DietRecord> mItems = new ArrayList<>();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        DietRecord record = data.getValue(DietRecord.class);
                        mItems.add(record);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        sprofileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                if(uri.getPath()==null){
                    profileImg.setImageResource(R.drawable.profile);
                }else{
                    profileImg.setImageURI(uri);
                    Thread mTread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(uri.toString());
                                HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                                conn.setDoInput(true);
                                conn.connect();
                                InputStream is = conn.getInputStream();
                                bitmap = BitmapFactory.decodeStream(is);
                            }catch (MalformedURLException e){
                                e.printStackTrace();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    };
                    mTread.start();

                    try {
                        mTread.join();
                        profileImg.setImageBitmap(bitmap);

                    }catch (InterruptedException e ){
                        e.printStackTrace();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profileImg.setImageResource(R.drawable.profile);
            }
        })
        */;

        sprofileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri.getPath().equals(null)){
                    profileImg.setImageResource(R.drawable.ic_launcher_icon);
                }else{
                    try{
                        Glide.with(MyPageFragment.this.getActivity()).using(new FirebaseImageLoader())
                                .load(sprofileRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(profileImg);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profileImg.setImageResource(R.drawable.ic_launcher_icon);

            }
        });

        childNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                txName.setText(name+" 님");
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
        childPWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Pweight= dataSnapshot.getValue(String.class);
                txPWeight.setText(Pweight+"kg");
                rPweight = Double.parseDouble(Pweight);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childMWeightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Mweight = dataSnapshot.getValue(String.class);
                txMWeight.setText(Mweight+"kg");
                rMweight = Double.parseDouble(Mweight);
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
                    Double BMR =  ((13.7*(rPweight*0.8))+(5*rHeight)-(6.8*rAge))*1.375;
                    Double changeweight = BMR*((rMweight - rPweight)/90);
                    rCalorie = BMR.intValue()+changeweight.intValue();

                    txBMR.setText(BMR.intValue()+" kcal");
                    txCalorie.setText(rCalorie+" kcal");
                }
                else{
                    Double BMR =  ((9.48*(rPweight*0.8))+(1.85*rHeight)-(4.7*rAge)+655)*1.2;
                    Double changeweight = BMR*((rMweight - rPweight)/90);
                    rCalorie = BMR.intValue()+changeweight.intValue();

                    txBMR.setText(BMR.intValue()+" kcal");
                    txCalorie.setText(rCalorie+" kcal");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    void photoAlbum(){
        //저장된 사진을 불러오는 함수이다. 즉앨범에있는것인데 인텐트는 ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        //갤러리리의 기본설정 해주도록하자!아래는이미지와그경로를 표준타입으로 설정한다.
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        //사진이 저장된위치(sdcard)에 데이터가 잇다고 지정
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_PHOTO_ALBUM);
    }



    private void signOut() {
        mAuth.signOut();
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode==REQUEST_PHOTO_ALBUM){
                //앨범에서 호출한경우 data는 이전인텐트(사진갤러리)에서 선택한 영역을 가져오게된다.
                UploadTask uploadTask = sprofileRef.putFile(data.getData());
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyPageFragment.this.getActivity(), "프로필사진업로드 실패!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        try{
                            Glide.with(MyPageFragment.this.getActivity()).using(new FirebaseImageLoader())
                                    .load(sprofileRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(profileImg);
                        }catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });

            }
        }
    }
    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        if (activityResultEvent.getResultCode()==-1){
        }
    }
}
