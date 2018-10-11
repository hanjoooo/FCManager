package com.example.khanj.fcmanager.ManagePage;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.khanj.fcmanager.Base.BaseFragment;
import com.example.khanj.fcmanager.Model.DietRecord;
import com.example.khanj.fcmanager.Model.FoodCalroie;
import com.example.khanj.fcmanager.R;
import com.example.khanj.fcmanager.adapter.FoodListAdapter;
import com.example.khanj.fcmanager.event.ActivityResultEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */

public class ManagementFragment extends BaseFragment  {

    private Realm mRealm;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("users");
    private DatabaseReference mFoodRef=mRootRef.child("food");
    private DatabaseReference mchildRef;
    private DatabaseReference IntakeRef;
    private DatabaseReference stepRef;
    private DatabaseReference mchildpCalRef;
    private DatabaseReference FoodRecordRef;

    private RecyclerView recyclerView;

    private TextView txmCal;
    private TextView txpCal;
    private TextView txexCal;
    private TextView txwGain;
    private TextView txdate;

    private int todaypCal;
    private int excal=0;

    private ArrayList<FoodCalroie> foodItems = new ArrayList<>();
    private ArrayList<FoodCalroie> foodtempItems = new ArrayList<>();
    private FoodListAdapter adapter;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private LinearLayout linearLayout;
    private LinearLayout nolistdata;
    Cursor cursor;
    MaterialCalendarView materialCalendarView;

    ArrayList<DietRecord> mItems = new ArrayList<>();
    DietRecord mItem = new DietRecord();
    String shot_Day =" ";
    int record_Ok=0;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_management, container, false);
        mRealm = Realm.getDefaultInstance();
        materialCalendarView = (MaterialCalendarView)v.findViewById(R.id.calendarView);

        linearLayout = v.findViewById(R.id.linaerview);
        nolistdata=v.findViewById(R.id.no_listdata);
        linearLayout.setVisibility(View.GONE);
        nolistdata.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        String[] result = {"2017,03,18","2017,04,18","2017,05,18","2017,06,18"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "년 ");
                Log.i("Month test", Month + "월 ");
                Log.i("Day test", Day + "일");

                if(Month  <10 && Day<10){
                    shot_Day = Year + "년 " +"0"+ Month + "월 " +"0"+ Day+"일";
                }else if(Month< 10){
                    shot_Day = Year + "년 " +"0"+Month + "월 " + Day+"일";
                }else if(Day<10){
                    shot_Day = Year + "년 " + Month + "월 " +"0"+ Day+"일";
                }else{
                    shot_Day = Year + "년 " + Month + "월 " + Day+"일";
                }

                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();

                for(DietRecord a:mItems){
                    if(a.getDate().equals(shot_Day)){
                        record_Ok=1;
                        mItem= a;
                        break;
                    }
                    record_Ok=0;
                }
                if(record_Ok==1){
                    setCalRecord(shot_Day);
                    //MyAlertDialogFragment dialog = new MyAlertDialogFragment().newInstance(shot_Day,mItem);
                    //dialog.show(getActivity().getFragmentManager(),"dialog");
                }else{
                    foodItems.clear();
                    adapter.notifyDataSetChanged();
                    txdate.setText(shot_Day+" - 일지");
                    nolistdata.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    //MyAlertDialogFragment dialog = new MyAlertDialogFragment().newInstance(shot_Day);
                    //dialog.show(getActivity().getFragmentManager(),"dialog");
                }
                record_Ok=0;
            }
        });


        txmCal = (TextView)v.findViewById(R.id.mkcal);
        txpCal = (TextView)v.findViewById(R.id.pkcal);
        txexCal =(TextView)v.findViewById(R.id.exkcal);
        txwGain = (TextView) v.findViewById(R.id.weightgain);
        txdate = v.findViewById(R.id.date);

        adapter = new FoodListAdapter(foodItems);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));



        return v;
        //
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            //materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,ManagementFragment.this.getActivity()));

        }
    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        if (activityResultEvent.getResultCode()==-1){
        }
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(String title){
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("pweight","기록없음");
            args.putString("mweight","기록없음");
            args.putString("mkcal","기록없음");
            args.putString("pkcal","기록없음");
            args.putString("bmr","기록없음");
            frag.setArguments(args);
            return frag;
        }
        public static MyAlertDialogFragment newInstance(String title,DietRecord dietRecord){
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("pweight",dietRecord.getpWeight().toString()+" kg");
            args.putString("mweight",dietRecord.getmWeight().toString()+" kg");
            args.putString("mkcal",String.valueOf(dietRecord.getmCal())+" kcal");
            args.putString("pkcal",String.valueOf(dietRecord.getpCal())+" kcal");
            args.putString("bmr",String.valueOf(dietRecord.getBmr())+" kcal");
            frag.setArguments(args);
            return frag;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);
            String title = getArguments().getString("title");
            String pweight = getArguments().getString("pweight");
            String mweight = getArguments().getString("mweight");
            String mkcal = getArguments().getString("mkcal");
            String pkcal = getArguments().getString("pkcal");
            String bmr= getArguments().getString("bmr");
            return new AlertDialog.Builder(getActivity())
                  //  .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title+"  기록")
                    .setMessage("\n현재 몸무게  :  "+pweight+"\n\n"+"목표 몸무게  :  "+mweight+"\n\n"+"기초 대사량  :  "+bmr+"\n\n"+
                            "권장 섭취량  :  "+mkcal+"\n\n"+"실제 섭취량  :  "+pkcal+"")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("MyLog", "확인 버튼이 눌림");
                        }
                    })
                    .create();
        }
    }

    public void setCalRecord(String date){
        nolistdata.setVisibility(View.GONE);
        txdate.setText(date + " - 일지");
        todaypCal = mItem.getpCal();
        excal = mItem.getExCal();
        txpCal.setText(mItem.getpCal() + " kcal");
        txmCal.setText(mItem.getmCal() + " kcal");
        txexCal.setText(mItem.getExCal() + " kcal");
        Double weightGain = ((mItem.getpCal() - mItem.getmCal() - mItem.getExCal()) / Double.valueOf(mItem.getBmr()));
        weightGain = Double.parseDouble(String.format("%.4f", weightGain));
        txwGain.setText(weightGain + " kg");
        setFoodRecord(date);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void setFoodRecord(String date){
        FoodRecordRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodItems.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        FoodCalroie record = data.getValue(FoodCalroie.class);
                        foodItems.add(record);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mchildRef = mConditionRef.child(currentUser.getUid());
        FoodRecordRef =mFoodRef.child(currentUser.getUid());
        IntakeRef = mchildRef.child("DietRecord");
        stepRef = mchildRef.child("curStep");
        IntakeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long now = System.currentTimeMillis();
                final Date date = new Date(now);
                // 출력될 포맷 설정
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                mItems.clear();
                txdate.setText(simpleDateFormat.format(date)+" - 일지");
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    DietRecord record = data.getValue(DietRecord.class);
                    mItems.add(record);

                }
                for(DietRecord x : mItems){
                    if(x.getDate().equals(simpleDateFormat.format(date))){
                        mItem= x;
                        setCalRecord(x.getDate());
                        nolistdata.setVisibility(View.GONE);
                        break;
                    }
                    else{
                        nolistdata.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
