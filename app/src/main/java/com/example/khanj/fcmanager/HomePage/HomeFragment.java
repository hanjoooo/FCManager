package com.example.khanj.fcmanager.HomePage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.khanj.fcmanager.Base.BaseFragment;
import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.Service.StepCheckService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

import static android.content.Context.BIND_AUTO_CREATE;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference IntakeRef;
    private DatabaseReference stepRef;


    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE=1;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM=2;

    ImageView iv;
    private TextView txmCal;
    private TextView txpCal;
    private TextView txexCal;
    private TextView txwGain;


    private TextView txstep;

    private int excal=0;
    private String filepath=null;
    private Realm mRealm;
    private StepCheckService mService;
    private boolean isBind;

    private Animation fab_open,fab_close;
    private TextView txfab1,txfab2;
    private Boolean isFabOpen=false;
    private FloatingActionButton fab,fab1,fab2;

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
        txfab1 =(TextView)v.findViewById(R.id.txfab1);
        txfab2=(TextView)v.findViewById(R.id.txfab2);
        txfab1.setVisibility(View.GONE);
        txfab2.setVisibility(View.GONE);

        //가져올 사진의 이름을 정한다.
        //v.findViewById(R.id.getCustom).setOnClickListener(this);
        v.findViewById(R.id.camerabutton).setOnClickListener(this);
        v.findViewById(R.id.fab1).setOnClickListener(this);
        v.findViewById(R.id.fab2).setOnClickListener(this);

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

        return v;
        //
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        IntakeRef = mchildRef.child("DietRecord");
        stepRef = mchildRef.child("curStep");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        IntakeRef.child(simpleDateFormat.format(date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DietRecord dietRecord = dataSnapshot.getValue(DietRecord.class);
                    excal = dietRecord.getExCal();
                    txpCal.setText(dietRecord.getpCal()+" kcal");
                    txmCal.setText(dietRecord.getmCal()+" kcal");
                    txexCal.setText(dietRecord.getExCal()+" kcal");
                    Double weightGain = ((dietRecord.getpCal()-dietRecord.getmCal()-dietRecord.getExCal())/Double.valueOf(dietRecord.getBmr()));
                    weightGain = Double.parseDouble(String.format("%.4f",weightGain));
                    txwGain.setText(weightGain+" kg");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        stepRef.addValueEventListener(new ValueEventListener() {
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
    }
    public void anim(){
        if(isFabOpen){
            fab.setImageResource(R.drawable.camerapressed);
            txfab1.setVisibility(View.GONE);
            txfab2.setVisibility(View.GONE);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen=false;
        }else{
            fab.setImageResource(R.drawable.camera_remove);
            txfab1.setVisibility(View.VISIBLE);
            txfab2.setVisibility(View.VISIBLE);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
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

            if(requestCode==REQUEST_PHOTO_ALBUM){
                //앨범에서 호출한경우 data는 이전인텐트(사진갤러리)에서 선택한 영역을 가져오게된다.
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(data.getData(),projection,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String s=cursor.getString(column_index);
                filepath = s;
                cursor.close();
                iv.setImageURI(data.getData());
            }
        }

    }

}