package com.example.iopd;

import android.Manifest;
import android.app.Fragment;
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

public class MainMenuActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ViewPager mViewPage;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    public static String tvLogi, tvLati;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static Double lati1, lati2, logi1, logi2;
    private boolean queue, area;
    private FirebaseInstanceIdService firebaseInstanceIdService;

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
                case R.id.action_navigation:

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

        mViewPage = findViewById(R.id.fragment);
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        setupViewPager(mViewPage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                if(latitude >= lati1 && latitude <= lati2 && longitude >= logi1 && longitude <= logi2){
                    area = true;
                    //Call function request for notification

                }
                Log.i("Location  aaaaa", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);

        firebaseInstanceIdService = new FirebaseInstanceIdService();


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
            mViewPage.setAdapter(adapter);
        }else if(page == 1){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new PlaceFragment(),"Suggestion location");
            mViewPage.setAdapter(adapter);
        }else if(page == 2){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new NotificationFragment(),"Notification");
            mViewPage.setAdapter(adapter);
        }else if(page == 3){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new ProcessFragment(),"Progress");
            mViewPage.setAdapter(adapter);
        }else if(page == 4){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new Place2Fragment(),"Progress");
            mViewPage.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

}
