package com.example.khanj.fcmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.khanj.fcmanager.HomePage.HomeFragment;
import com.example.khanj.fcmanager.MyPage.MyPageFragment;
import com.example.khanj.fcmanager.event.ActivityResultEvent;
import com.example.khanj.fcmanager.handler.BackPressHandler;

import com.example.khanj.fcmanager.ManagePage.ManagementFragment;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    private MenuItem preitem;
    private TextView txt_title;
    private BackPressHandler backPressHandler;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item==preitem) return false;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    replaceViewPager(new HomeFragment());
                    txt_title.setText("FC MANAGER");
                    preitem = item;
                    return true;
                case R.id.nav_cardlist:
                    replaceViewPager(new ManagementFragment());
                    txt_title.setText("FC MANAGER");
                    preitem = item;
                    return true;
                case R.id.nav_mypage:
                    replaceViewPager(new MyPageFragment());
                    txt_title.setText("FC MANAGER");
                    preitem = item;
                    return true;
            }
            return false;
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_title = findViewById(R.id.txt_main_title);

        Realm.init(this);
        replaceViewPager(new HomeFragment());
        txt_title.setText("FC MANAGER");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        backPressHandler = new BackPressHandler(this);
    }

    private boolean replaceViewPager(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .commit();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public void onBackPressed(){
        backPressHandler.onBackPressed();
    }
}
