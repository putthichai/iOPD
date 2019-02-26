package com.example.iopd;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "";
    private TextView mTextMessage;
    private ViewPager mViewPage;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    public static String tvLogi, tvLati;
    private static Double lati1, lati2, logi1, logi2;
    private static boolean queue;
    private boolean area;
    private FirebaseInstanceIdService firebaseInstanceIdService;
    private int backButtonCount;
    private int currentPage;
    private TextView right;
    private GPSTracker gps;
    private static Patient patient;
    private LocationListener locationListener;
    private LocationManager locationManager;

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

        //create patient
        patient = new Patient(2, "test", "test");


        //set area of the hospital
        lati1 = 0.0;
        lati2 = 10.0;
        logi1 = -100.0;
        logi2 = 50.0;
        area = false;
        queue = false;
        backButtonCount =0;
        currentPage =0;

        //setup page
        mViewPage = findViewById(R.id.fragment);
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        setupViewPager(mViewPage);
        right = findViewById(R.id.right);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        firebaseInstanceIdService = new FirebaseInstanceIdService();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                Log.i("aaaaaaa Bookmark","aaaaaaaa "+location.getLatitude()+"     "+location.getLongitude());
                if(latitude >= lati1 && latitude <= lati2 && longitude >= logi1 && longitude <= logi2){
                    area = true;
                    //Call function request for notification
                    bookmarkQueue();
                    //Log.i("Location  aaaaa", location.toString());

                }

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



            //new BookmarkQueue(this).execute();
    /*        gps = new GPSTracker(this);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                gps.onLocationChanged(gps.getLocation());
                Log.d(TAG,"aaaaaaaaaa time+++ ");


            }
        }, 0, 5000);
            gps.onLocationChanged(gps.getLocation());
            checklocation();
            //right.setText(latitude+"  "+longitude);
            //bookmarkQueue();*/
    }


    //first setup
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(),"Home");
        viewPager.setAdapter(adapter);
    }

    //change page
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


    //set value back bottom
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

    protected static void bookmarkQueue()  {
        Log.i("aaaaaaa Bookmark","aaaaaaa Bookmark");
        CallApi getAppointment = new CallApi(patient.getId());
        getAppointment.execute("getAppointmentList");
        int temp = getAppointment.getAppointmentId();
        Log.i("aaaaaaa Bookmark","aaaaaaa Bookmark "+temp);
        if(temp == -1){

        }else {
            if(queue == false){
                CallApi getRoomScheduleByPatientId =  new CallApi(patient.getId());
                getRoomScheduleByPatientId.execute("getRoomScheduleByPatientId");
                queue = true;
                Log.d(TAG,"aaaaaaa have appointment");
            }
        }

        // Toast.makeText(this, ""+appointmentId, Toast.LENGTH_SHORT).show();



        //String temp = getAppointment.jobj.toString(1);

       // temp = getAppointment.getvalue();

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
