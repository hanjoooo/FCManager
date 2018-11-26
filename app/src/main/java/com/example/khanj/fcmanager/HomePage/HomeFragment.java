package com.example.khanj.fcmanager.HomePage;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.khanj.fcmanager.Dialog.WeightDialog;
import com.example.khanj.fcmanager.Helper.WeightDialogListener;
import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.Model.FoodCalroie;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.Service.StepCheckService;

import com.example.khanj.fcmanager.adapter.FoodListAdapter;
import com.example.khanj.fcmanager.loading.LoadingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.realm.Realm;

import static android.content.Context.BIND_AUTO_CREATE;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends LoadingFragment implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mFoodRef=mRootRef.child("food");
    private DatabaseReference mFoodexistRef = mRootRef.child("foodexist");
    private DatabaseReference mFoodlistRef = mRootRef.child("foodlist");
    private DatabaseReference mFoodlist3Ref = mRootRef.child("foodlist3");
    private DatabaseReference mchildRef;
    private DatabaseReference IntakeRef;
    private DatabaseReference childPWeightRef;
    private DatabaseReference childMWeightRef;
    private DatabaseReference childGenderRef;
    private DatabaseReference childAgeRef;
    private DatabaseReference childHeightRef;
    private DatabaseReference childStepRef;
    private DatabaseReference childIntakeRef;
    private DatabaseReference mchildpCalRef;
    private DatabaseReference FoodRecordRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean refreshing=false;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE=1;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM=2;
    //바코드 스캔시 돌려받을 번호
    static int REQUEST_BARCODE=3;

    ImageView iv;
    private TextView txmCal;
    private TextView txpCal;
    private TextView txexCal;
    private TextView txwGain;


    private TextView txstep;

    private String filepath=null;
    private Realm mRealm;
    private StepCheckService mService;
    private boolean isBind;

    private Animation fab_open,fab_close;
    private TextView txfab1,txfab2,txfab3;
    private Boolean isFabOpen=false;
    private FloatingActionButton fab,fab1,fab2,fab3;

    private RecyclerView recyclerView;
    private ArrayList<FoodCalroie> mItems = new ArrayList<>();
    private FoodListAdapter adapter;

    private int todaypCal;
    private int excal=0;
    private int exKcal=0;

    private int rAge=0;
    private Double rHeight=0.0;
    private Double pWeight=0.0;
    private Double mWeight=0.0;
    private Double BMR=0.0;
    private int pCalorie = 0;
    private int mCalorie = 0;

    ArrayList<String> a= new ArrayList<>();
    ArrayList<String> foodCandinate= new ArrayList<>();
    Map<String,Integer> foodlayermap = new HashMap<String, Integer>();

    private Drawable fooddrawble;

    ServiceConnection soonn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCheckService.MyBinder myBinder = (StepCheckService.MyBinder) service;
            mService = myBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBind = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRealm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        txmCal = (TextView)v.findViewById(R.id.mkcal);
        txpCal = (TextView)v.findViewById(R.id.pkcal);
        txexCal =(TextView)v.findViewById(R.id.exkcal);
        txstep = (TextView) v.findViewById(R.id.txstep);
        txwGain = (TextView) v.findViewById(R.id.weightgain);

        //여기에 일단 기본적인 이미지파일 하나를 가져온다.
        iv=(ImageView) v.findViewById(R.id.imgView);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab =(FloatingActionButton)v.findViewById(R.id.camerabutton);
        fab1 = (FloatingActionButton)v.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)v.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton)v.findViewById(R.id.fab3);
        txfab1 =(TextView)v.findViewById(R.id.txfab1);
        txfab2=(TextView)v.findViewById(R.id.txfab2);
        txfab3=(TextView)v.findViewById(R.id.txfab3);
        txfab1.setVisibility(View.GONE);
        txfab2.setVisibility(View.GONE);
        txfab3.setVisibility(View.GONE);
        //가져올 사진의 이름을 정한다.
        //v.findViewById(R.id.getCustom).setOnClickListener(this);
        v.findViewById(R.id.camerabutton).setOnClickListener(this);
        v.findViewById(R.id.fab1).setOnClickListener(this);
        v.findViewById(R.id.fab2).setOnClickListener(this);
        v.findViewById(R.id.fab3).setOnClickListener(this);
        getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

        txstep.setText("오늘의 걸음 수  :  "+0);


        getActivity().startService(new Intent(HomeFragment.this.getActivity(),StepCheckService.class));
        getActivity().bindService(new Intent(HomeFragment.this.getActivity(), StepCheckService.class), soonn, BIND_AUTO_CREATE);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:366b14a8-de76-447e-b370-751ccb6a8da8", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        adapter = new FoodListAdapter(mItems);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));



        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(filepath==null){

                }else{
                    final File file = new File(filepath);
                    TransferObserver observer = transferUtility.upload(
                            "s3fcmanager", /* 업로드 할 버킷 이름 */
                            "picture", /* 버킷에 저장할 파일의 이름 */
                            file/* 버킷에 저장할 파일 */
                    );
                    Toast.makeText(getActivity(), "사진을 등록하였습니다.", Toast.LENGTH_SHORT).show();
                    // Amazon Cognito 인증 공급자를 초기화합니다
                }
                return false;
            }
        });

        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            int cameraPermission = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);


            if ( cameraPermission == PackageManager.PERMISSION_GRANTED
                    && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {


            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Snackbar.make(v, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions( HomeFragment.this.getActivity(), REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();


                } else {
                    // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions( this.getActivity(), REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

        } else {

            final Snackbar snackbar = Snackbar.make(v, "디바이스가 카메라를 지원하지 않습니다.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
        return v;
        //
    }
    //찍은 음식개수 선택하는 다이얼로그
    void chooseNumberoftimes(final Uri foodimg){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                HomeFragment.this.getActivity());
        alertBuilder.setTitle("음식의 갯수를 선택해주세요 \n(최대 5개 선택가능)");
        final ArrayAdapter<String> layeradapter = new ArrayAdapter<String>(
                HomeFragment.this.getActivity(),
                android.R.layout.select_dialog_item);

        layeradapter.clear();
        for(int i=1;i<=5;i++){
            layeradapter.add(i+" 개");
        }
        // 버튼 생성
        alertBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        alertBuilder.setAdapter(layeradapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FoodChooseDialog(foodimg,which+1,1);
            }
        });
        alertBuilder.show();
    }

    void FoodChooseDialog(final Uri foodimg, final int itemnumbofitems, final int itemindex){
        if(itemindex > itemnumbofitems ){
            return;
        }
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH시 mm분 ss초");
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                HomeFragment.this.getActivity());

        try{
            InputStream inputStream = getActivity().getContentResolver().openInputStream(foodimg);
            fooddrawble = Drawable.createFromStream(inputStream, foodimg.toString());
        }catch (FileNotFoundException e){
            fooddrawble = getResources().getDrawable(R.drawable.ic_launcher_icon);
        }
        //alertBuilder.setIcon(fooddrawble);
        alertBuilder.setIcon(R.drawable.ic_launcher_icon);
        alertBuilder.setTitle("[ "+itemindex+"번째 음식을 선택해 주세요 ]");
        final ArrayAdapter<String> layer1adapter = new ArrayAdapter<String>(
                HomeFragment.this.getActivity(),
                android.R.layout.select_dialog_singlechoice);
        a.clear();
        foodCandinate.clear();
        foodlayermap.clear();
        a.add("Apple");
        a.add("grape");
        a.add("Banana");
        a.add("Cherry");
        a.add("Burger");
        for(int i=0;i<a.size();i++) {
            final int finalI = i;
            mFoodexistRef.child(a.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        layer1adapter.add(a.get(finalI));
                        foodlayermap.put(a.get(finalI), Integer.parseInt(dataSnapshot.getValue().toString()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        final double[] weightpersent = {1};

        // 버튼 생성
        alertBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        // Adapter 셋팅
        final Drawable finalFooddrawble = fooddrawble;
        alertBuilder.setAdapter(layer1adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // AlertDialog 안에 있는 AlertDialog
                        final String strName = layer1adapter.getItem(id);

                        //음식데이터가 layer1일 경우
                        if(foodlayermap.get(strName).equals(1)){
                            mFoodlistRef.child(strName).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final FoodCalroie record = dataSnapshot.getValue(FoodCalroie.class);
                                    final int firstweight  = record.getfWeight();

                                    WeightDialog weightDialog = new WeightDialog(HomeFragment.this.getActivity(),record.getfName(),record.getfWeight(),foodimg);
                                    weightDialog.setDialogListener(new WeightDialogListener() {
                                        @Override
                                        public void onPositiveClicked(int Weight) {
                                            int lastweight = Weight;
                                            weightpersent[0] = (double)lastweight/(double)firstweight;
                                            FoodCalroie newrecord = new FoodCalroie(record.getfName(),(int)((double)record.getfCal()*(weightpersent[0])),
                                                    Math.round(record.getfCarbs()*weightpersent[0] * 100d)/100d,Math.round(record.getfFat()*weightpersent[0] * 100d)/100d,
                                                    Math.round(record.getfProtiens()*weightpersent[0]*100d)/100d,Math.round(record.getfNa()*weightpersent[0]*100d)/100d,
                                                    (int)((double)record.getfWeight()*weightpersent[0]),foodimg.toString());
                                            mItems.add(newrecord);
                                            adapter.notifyDataSetChanged();

                                            todaypCal+=(newrecord.getfCal());
                                            mchildpCalRef.setValue(todaypCal);
                                            FoodRecordRef.child(simpleDateFormat.format(date)).child(simpleDateFormat1.format(date)).setValue(newrecord);
                                            FoodChooseDialog(foodimg,itemnumbofitems,itemindex+1);
                                        }
                                    });
                                    weightDialog.show();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        //음식데이터가 layer3일 경우
                        else if(foodlayermap.get(strName).equals(3)){
                            mFoodlist3Ref.child(strName).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        final AlertDialog.Builder alertlayer3Builder = new AlertDialog.Builder(
                                                HomeFragment.this.getActivity());
                                        //alertBuilder.setIcon(fooddrawble);
                                        alertlayer3Builder.setIcon(R.drawable.ic_launcher_icon);
                                        alertlayer3Builder.setTitle(strName);
                                        final ArrayAdapter<String> layer3adapter = new ArrayAdapter<String>(
                                                HomeFragment.this.getActivity(),
                                                android.R.layout.select_dialog_singlechoice);
                                        for(DataSnapshot data:dataSnapshot.getChildren()){
                                            layer3adapter.add(data.getValue().toString());
                                        }
                                        alertlayer3Builder.setAdapter(layer3adapter, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //회사 선택 시 일어나는 코드
                                                final String strName = layer3adapter.getItem(which);
                                                mFoodlistRef.child(strName).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            FoodCalroie record;
                                                            final AlertDialog.Builder alertlayer2Builder = new AlertDialog.Builder(
                                                                    HomeFragment.this.getActivity());
                                                            alertlayer2Builder.setIcon(R.drawable.ic_launcher_icon);
                                                            //alertBuilder.setIcon(fooddrawble);
                                                            alertlayer2Builder.setTitle(strName);
                                                            final ArrayAdapter<String> layer2adapter = new ArrayAdapter<String>(
                                                                    HomeFragment.this.getActivity(),
                                                                    android.R.layout.select_dialog_singlechoice);
                                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                record = data.getValue(FoodCalroie.class);
                                                                layer2adapter.add(record.getfName());
                                                            }

                                                            alertlayer2Builder.setAdapter(layer2adapter, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //layer2선택시
                                                                    final String foodName = layer2adapter.getItem(which);

                                                                    mFoodlistRef.child(strName).child(foodName).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            final FoodCalroie layer3data = dataSnapshot.getValue(FoodCalroie.class);
                                                                            final int firstweight  = layer3data.getfWeight();

                                                                            WeightDialog weightDialog = new WeightDialog(HomeFragment.this.getActivity(),layer3data.getfName(),layer3data.getfWeight(),foodimg);
                                                                            weightDialog.setDialogListener(new WeightDialogListener() {
                                                                                @Override
                                                                                public void onPositiveClicked(int Weight) {
                                                                                    int lastweight = Weight;
                                                                                    weightpersent[0] = (double)lastweight/(double)firstweight;
                                                                                    FoodCalroie newrecord = new FoodCalroie(layer3data.getfName(),(int)((double)layer3data.getfCal()*(weightpersent[0])),
                                                                                            Math.round(layer3data.getfCarbs()*weightpersent[0] * 100d)/100d,Math.round(layer3data.getfFat()*weightpersent[0] * 100d)/100d,
                                                                                            Math.round(layer3data.getfProtiens()*weightpersent[0]*100d)/100d,Math.round(layer3data.getfNa()*weightpersent[0]*100d)/100d,
                                                                                            (int)((double)layer3data.getfWeight()*weightpersent[0]),foodimg.toString());
                                                                                    mItems.add(newrecord);
                                                                                    adapter.notifyDataSetChanged();

                                                                                    todaypCal+=(newrecord.getfCal());
                                                                                    mchildpCalRef.setValue(todaypCal);
                                                                                    FoodRecordRef.child(simpleDateFormat.format(date)).child(simpleDateFormat1.format(date)).setValue(newrecord);
                                                                                    FoodChooseDialog(foodimg,itemnumbofitems,itemindex+1);
                                                                                }
                                                                            });
                                                                            weightDialog.show();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                            alertlayer2Builder.show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });

                                        alertlayer3Builder.show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
        alertBuilder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        FoodRecordRef =mFoodRef.child(currentUser.getUid());
        IntakeRef = mchildRef.child("DietRecord");
        childHeightRef = mchildRef.child("height");
        childAgeRef = mchildRef.child("age");
        childGenderRef = mchildRef.child("gender");
        childPWeightRef = mchildRef.child("Pweight");
        childMWeightRef = mchildRef.child("Mweight");
        childStepRef = mchildRef.child("curStep");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        FoodRecordRef.child(simpleDateFormat.format(date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mItems.clear();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        FoodCalroie record = data.getValue(FoodCalroie.class);
                        mItems.add(record);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        IntakeRef.child(simpleDateFormat.format(date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mchildpCalRef=IntakeRef.child(simpleDateFormat.format(date)).child("pCal");
                    DietRecord dietRecord = dataSnapshot.getValue(DietRecord.class);
                    todaypCal = dietRecord.getpCal();
                    excal = dietRecord.getExCal();
                    txpCal.setText(dietRecord.getpCal()+" kcal");
                    txmCal.setText(dietRecord.getmCal()+" kcal");
                    txexCal.setText(dietRecord.getExCal()+" kcal");
                    Double weightGain = ((dietRecord.getpCal()-dietRecord.getmCal()-dietRecord.getExCal())/Double.valueOf(dietRecord.getBmr()));
                    weightGain = Double.parseDouble(String.format("%.4f",weightGain));
                    txwGain.setText(weightGain+" kg");
                }
                else{

                    childStepRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(dataSnapshot.getValue().toString());
                            Double stepCal = val*0.03+exKcal;

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
                            pWeight = Double.parseDouble(Pweight);
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
                            }
                            else{
                                BMR =  ((9.48*(pWeight*0.8))+(1.85*rHeight)-(4.7*rAge)+655)*1.2;
                                Double changeweight = BMR*((mWeight - pWeight)/90);
                                mCalorie = BMR.intValue()+changeweight.intValue();
                            }
                            pCalorie = 0;
                            int excal = 0;
                            String today= simpleDateFormat.format(date);
                            DietRecord dietRecords = new DietRecord(today,pWeight,mWeight,mCalorie,pCalorie,BMR.intValue(),excal);
                            childIntakeRef = IntakeRef.child(dietRecords.getDate());
                            childIntakeRef.setValue(dietRecords);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childStepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txstep.setText("걸음 수  :  "+dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v){
        //첫번째로 사진가져오기를 클릭하면 또다른 레이아웃것을 다이어로그로 출력해서
        //선택하게끔 하자 !!!!
        if(v.getId()==R.id.camerabutton){
            anim();
        }
        else if(v.getId()==R.id.fab1){
            photoAlbum();
            anim();
        }
        else if(v.getId()==R.id.fab2) {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent, REQUEST_PICTURE);
            anim();
        }
        else if(v.getId()==R.id.fab3){
            Intent intent = new Intent(getActivity(), BarcodeScanActivity.class);
            startActivityForResult(intent, REQUEST_BARCODE);
            anim();
        }
    }
    public void anim(){
        if(isFabOpen){
            fab.setImageResource(R.drawable.camerapressed);
            txfab1.setVisibility(View.GONE);
            txfab2.setVisibility(View.GONE);
            txfab3.setVisibility(View.GONE);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen=false;
        }else{
            fab.setImageResource(R.drawable.camera_remove);
            txfab1.setVisibility(View.VISIBLE);
            txfab2.setVisibility(View.VISIBLE);
            txfab3.setVisibility(View.VISIBLE);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen=true;
        }
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
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK){
            if(requestCode==REQUEST_PICTURE){
                //사진을 찍은경우 그사진을 로드해온다.
                //iv.setImageBitmap(loadPicture());
                String path = data.getExtras().getString("path");
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(Uri.parse(path),projection,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String s=cursor.getString(column_index);
                filepath = s;
                cursor.close();
                iv.setImageURI(Uri.parse(path));
            }

            else if(requestCode==REQUEST_PHOTO_ALBUM){
                //앨범에서 호출한경우 data는 이전인텐트(사진갤러리)에서 선택한 영역을 가져오게된다.
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(data.getData(),projection,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String s=cursor.getString(column_index);
                filepath = s;
                cursor.close();
                //iv.setImageURI(data.getData());
                chooseNumberoftimes(data.getData());
            }
            else if(requestCode==REQUEST_BARCODE) {
                long now = System.currentTimeMillis();
                final Date date = new Date(now);
                // 출력될 포맷 설정
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH시 mm분 ss초");
                String barcode = data.getExtras().getString("data");
                FoodCalroie foodCalroie= FoodCal(barcode);
                if(foodCalroie.getfName().equals("음식이름")){
                    Toast.makeText(getActivity(), "죄송합니다..등록되지 않은 제품입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    todaypCal+=foodCalroie.getfCal();
                    mchildpCalRef.setValue(todaypCal);
                    FoodRecordRef.child(simpleDateFormat.format(date)).child(simpleDateFormat1.format(date)).setValue(foodCalroie);
                }
            }

        }
    }

    public FoodCalroie FoodCal(String data){
        FoodCalroie foodCalroie;

        if(data.equals("8806002007298")){
            foodCalroie = new FoodCalroie("비타500",70,17,0,1,1,0,"img");
        }
        else{
            foodCalroie = new FoodCalroie();
        }

        return foodCalroie;
    }

}