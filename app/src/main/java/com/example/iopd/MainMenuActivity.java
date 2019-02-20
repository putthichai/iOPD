package com.example.iopd;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ViewPager mViewPage;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    public static String tvLogi, tvLati;
    private static Double lati1, lati2, logi1, logi2;
    private boolean queue, area;
    private FirebaseInstanceIdService firebaseInstanceIdService;
    private int backButtonCount;
    private int currentPage;
    private TextView right;
    private GPSTracker gps;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_home:
                    setViewPager(0);
                    return true;
                case R.id.action_notification:
                    setViewPager(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);

        lati1 = 0.0;
        lati2 = 10.0;
        logi1 = -100.0;
        logi2 = 50.0;
        area = false;
        queue = false;
        backButtonCount =0;
        currentPage =0;

        mViewPage = findViewById(R.id.fragment);
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        setupViewPager(mViewPage);
        right = findViewById(R.id.right);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        firebaseInstanceIdService = new FirebaseInstanceIdService();

            gps = new GPSTracker(this);
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //right.setText(latitude+"  "+longitude);

    }

    protected void checklocation(){
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        right.setText(latitude+"  "+longitude);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(),"Home");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int page){
        if(page == 0){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new HomeFragment(),"Home");
            currentPage = 0;
            mViewPage.setAdapter(adapter);
        }else if(page == 1){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new PlaceFragment(),"Suggestion location");
            mViewPage.setAdapter(adapter);
            currentPage = 1;
        }else if(page == 2){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new NotificationFragment(),"Notification");
            mViewPage.setAdapter(adapter);
            currentPage =2;
        }else if(page == 3){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new ProcessFragment(),"Progress");
            mViewPage.setAdapter(adapter);
            currentPage = 3;
        }else if(page == 4){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new Place2Fragment(),"Progress");
            mViewPage.setAdapter(adapter);
            currentPage = 4;
        }
    }


    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount =0;
            finish();
        }
        else if(currentPage == 0){
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }else  if(currentPage == 1){
            setViewPager(0);
            backButtonCount =0;
        }else  if(currentPage == 2){
            setViewPager(0);
            backButtonCount =0;
        }else  if(currentPage == 3){
            setViewPager(0);
            backButtonCount =0;
        }else  if(currentPage == 4){
            setViewPager(1);
            backButtonCount =0;
        }
    }

}
