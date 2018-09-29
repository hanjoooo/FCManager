package com.example.khanj.fcmanager.HomePage;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khanj.fcmanager.Base.BaseFragment;
import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.event.ActivityResultEvent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import jnr.ffi.annotations.In;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mchildRef;
    private DatabaseReference IntakeRef;


    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE=1;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM=2;
    //첫번째 이미지 아이콘 샘플 이다.
    static String SAMPLEIMG="ic_launcher.png";

    ImageView iv;
    ImageView cbutton;
    Dialog dialog;
    TextView txcLeft;
    private Realm mRealm;
    private PieChart mChart;

    private String[] Calorie = new String[]{"섭취한 칼로리","남은 칼로리"};
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mRealm = Realm.getDefaultInstance();

        mAuth = FirebaseAuth.getInstance();

        /*
        mChart = (PieChart)v.findViewById(R.id.piechart);
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setMaxAngle(180);
        mChart.setRotationAngle(180);
        mChart.setCenterTextOffset(0,-20);
        mChart.getDescription().setEnabled(false);
        setData(2,100);

        mChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        */

        //여기에 일단 기본적인 이미지파일 하나를 가져온다.
        iv=(ImageView) v.findViewById(R.id.imgView);
        cbutton= (ImageView)v.findViewById(R.id.camerabutton);
        txcLeft = (TextView)v.findViewById(R.id.leftcal);

        txcLeft.setSelected(true);

        //가져올 사진의 이름을 정한다.
        //v.findViewById(R.id.getCustom).setOnClickListener(this);
        v.findViewById(R.id.camerabutton).setOnClickListener(this);
        /*
        cbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        cbutton.setImageResource(R.drawable.pluscamera2);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        startActivityForResult(intent,REQUEST_PICTURE);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        cbutton.setImageResource(R.drawable.pluscamera);
                    }
                }
                return false;
            }
        });
        */
        return v;
        //
    }

    private void setData(int count,int range){
        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(80,Calorie[0]));
        values.add(new PieEntry(20,Calorie[1]));

        PieDataSet dataSet = new PieDataSet(values,"");
        dataSet.setSelectionShift(3f);
        dataSet.setSliceSpace(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.invalidate();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        IntakeRef = mchildRef.child("DietRecord");
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        IntakeRef.child(simpleDateFormat.format(date)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DietRecord dietRecord = dataSnapshot.getValue(DietRecord.class);
                    txcLeft.setText("오늘 남은 칼로리  :  "+dietRecord.getmCal()+"  -  " +dietRecord.getpCal()+"  =  "+(dietRecord.getmCal()-dietRecord.getpCal())+" kcal");
                    txcLeft.setSelected(true);
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
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent,REQUEST_PICTURE);
        }
        /*
        if(v.getId()==R.id.getCustom){

            //다이어로그를 먼저만들어낸다.
            AlertDialog.Builder builder=new AlertDialog.Builder(this.getContext());
            //이곳에 만드는 다이어로그의 layout 을 정한다.
            View customLayout=View.inflate(this.getContext(),R.layout.custom_button,null);
            //현재 빌더에 우리가만든 다이어로그 레이아웃뷰를 추가하도록하자!!
            builder.setView(customLayout);
            //다이어로그의 버튼에  카메라와 사진앨범의 클릭 기능을 넣어주자.
            customLayout.findViewById(R.id.camera).setOnClickListener(this);
            customLayout.findViewById(R.id.photoAlbum).setOnClickListener(this);
            //지금까지 만든 builder를 생성하고, 띄우자.!!!
            dialog=builder.create();
            dialog.show();
        }
        else if(v.getId()==R.id.camera){
            //카메라버튼인경우,일단 다이어로그를 끄고 사진을 찍는 함수를 불러오자
            dialog.dismiss();
            //takePicture();
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent,REQUEST_PICTURE);
        }else if(v.getId()==R.id.photoAlbum){
            //이경우역시 다이어로그를 끄고 앨범을 불러오는 함수를 불러오자!!
            dialog.dismiss();
            photoAlbum();
        }
        */
    }
    void takePicture(){
        //사진을 찍는 인텐트를 가져온다. 그인텐트는 MediaStore에있는
        // ACTION_IMAGE_CAPTURE를 활용해서 가져온다.
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //그후 파일을 지정해야하는데 File의 앞부분 매개변수에는 파일의 절대경로를 붙여야
        // 한다. 그러나 직접 경로를 써넣으면 sdcard접근이 안되므로 ,
        // Environment.getExternalStorageDirectory()로 접근해서 경로를 가져오고 두번째
        // 매개 변수에 파일이름을 넣어서 활용 하도록하자!!
        File file=new File(Environment.getExternalStorageDirectory(),SAMPLEIMG);



        //그다음에 사진을 찍을대 그파일을 현재 우리가 갖고있는 SAMPLEIMG로 저장해야
        // 한다. 그래서 경로를 putExtra를 이용해서 넣도록 한다. 파일형태로 말이다.
        // 그리고 실제로 이파일이 가리키는 경로는 /mnt/sdcard/ic_launcher)
        Uri uris= FileProvider.getUriForFile(getApplicationContext(),"com.example.khanj.fcmanager.fileprovider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uris);
        //그럼이제사진찍기 인텐트를 불러온다.
        startActivityForResult(intent,REQUEST_PICTURE);
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
    Bitmap loadPicture(){

        //사진찍은 것을 로드 해오는데 사이즈를 조절하도록하자!!일단 파일을 가져오고
        File file=new File(Environment.getExternalStorageDirectory(),SAMPLEIMG);

        //현재사진찍은 것을 조절하구이해서 조절하는 클래스를 만들자.
        BitmapFactory.Options options=new BitmapFactory.Options();
        //이제 사이즈를 설정한다.
        options.inSampleSize=4;
        //그후에 사진 조정한것을 다시 돌려보낸다.
        return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
    }


    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==getActivity().RESULT_OK){
            if(requestCode==REQUEST_PICTURE){
                //사진을 찍은경우 그사진을 로드해온다.
                //iv.setImageBitmap(loadPicture());
                String path = data.getExtras().getString("path");
                iv.setImageURI(Uri.parse(path));
            }

            if(requestCode==REQUEST_PHOTO_ALBUM){
                //앨범에서 호출한경우 data는 이전인텐트(사진갤러리)에서 선택한 영역을 가져오게된다.
                iv.setImageURI(data.getData());
            }
        }

    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        if(activityResultEvent.getRequestCode()==REQUEST_PICTURE){
            //사진을 찍은경우 그사진을 로드해온다.
            //iv.setImageBitmap(loadPicture());
        }
        else if(activityResultEvent.getRequestCode()==REQUEST_PHOTO_ALBUM){
            //앨범에서 호출한경우 data는 이전인텐트(사진갤러리)에서 선택한 영역을 가져오게된다.
            //iv.setImageURI(activityResultEvent.getData().getData());
        }
    }
}