package com.example.khanj.fcmanager;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.khanj.fcmanager.Base.BaseFragment;
import com.example.khanj.fcmanager.HomePage.HomeFragment;
import com.example.khanj.fcmanager.MyPage.MyPageFragment;
import com.example.khanj.fcmanager.event.ActivityResultEvent;
import com.example.khanj.fcmanager.handler.BackPressHandler;

import com.example.khanj.fcmanager.ManagePage.ManagementFragment;
import com.example.khanj.fcmanager.utils.FragmentHistory;
import com.example.khanj.fcmanager.views.FragNavController;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;




public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {
    private int preitem;
    String[] TABS;
    private Toolbar toolbar;
    private BackPressHandler backPressHandler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    switchTab(0);
                    if (preitem==0) {
                        mNavController.clearStack();
                        return false;
                    }
                    fragmentHistory.push(0);
                    preitem = 0;
                    return true;
                case R.id.nav_cardlist:
                    switchTab(1);
                    if (preitem==1) {
                        mNavController.clearStack();
                        return false;
                    }
                    fragmentHistory.push(1);
                    preitem = 1;

                    return true;
                case R.id.nav_mypage:
                    switchTab(2);
                    if (preitem==2) {
                        mNavController.clearStack();
                        return false;
                    }
                    fragmentHistory.push(2);
                    preitem = 2;
                    return true;
            }
            return false;
        }
    };

    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private  BottomNavigationView bottomTabLayout;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildView();

        fragmentHistory = new FragmentHistory();
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.main_activity_fragment_container)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();

        switchTab(0);

    }

    public void snackBarOn(String msg){
        this.msg = msg;
        snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void snackBarDismiss(){
        snackbar.setText(msg);
        snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }
        );
    }

    private void buildView(){
        TABS  = new String[]{getString(R.string.title_home), getString(R.string.title_cardlist), getString(R.string.title_mypage)};

        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.coordinator);
        bottomTabLayout = (BottomNavigationView) findViewById(R.id.navigation);
        bottomTabLayout.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        Realm.init(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }

    private void switchTab(int position) {
        mNavController.switchTab(position);
        // updateToolbarTitle(position);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();

        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();

            } else {
                if (fragmentHistory.getStackSize() > 1) {
                    int position = fragmentHistory.popPrevious();
                    switchTab(position);
                    //updateTabSelection(position);
                    bottomTabLayout.getMenu().getItem(position).setChecked(true);
                    preitem = position;
                } else {
                    bottomTabLayout.getMenu().getItem(0).setChecked(true);
                    switchTab(0);
                    preitem = 0;
                    //updateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case FragNavController.TAB1:
                return new HomeFragment();
            case FragNavController.TAB2:
                return new ManagementFragment();
            case FragNavController.TAB3:
                return new MyPageFragment();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            updateToolbar();
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            updateToolbar();
        }
    }



}