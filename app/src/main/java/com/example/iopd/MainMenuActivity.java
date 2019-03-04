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



public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "";
    private static int queueNo;
    private ViewPager mViewPage;
    private static boolean queue;
    private static String stateDoing, targetLocation;
    private static int remainQueue, tempconut;
    private FirebaseInstanceIdService firebaseInstanceIdService;
    private int backButtonCount;
    private int currentPage;
    private TextView right;
    private static Patient patient;
    protected static LocationListener locationListener;
    protected static LocationManager locationManager;
    private static HomeFragment home;
    private NotificationFragment notification;
    private ProcessFragment process;
    private PlaceFragment place;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
        queue = false;
        backButtonCount =0;
        currentPage =0;
        tempconut =0;

        //setup page
        mViewPage = findViewById(R.id.fragment);

        home = new HomeFragment();
        notification = new NotificationFragment();
        process = new ProcessFragment();
        place = new PlaceFragment();

        setupViewPager(mViewPage);
        right = findViewById(R.id.right);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        firebaseInstanceIdService = new FirebaseInstanceIdService();

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
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                    if(checkInArea(location) == true && queue == false){
                        //Log.d("111111","start bookmark queue "+queue+" queueNo "+queueNo);
                        bookmarkQueue();
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
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1, 0, locationListener);
    }


    //first setup
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(home,"Home");
        viewPager.setAdapter(adapter);
    }

    //change page
    public void setViewPager(int page){
        if(page == 0){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(home,"Home");
            home.onStart();
            place.onStop();
            notification.onStop();
            process.onStop();
            currentPage = 0;
            mViewPage.setAdapter(adapter);
        }else if(page == 1){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(place,"Suggestion location");
            home.onStop();
            place.onStart();
            notification.onStop();
            process.onStop();
            mViewPage.setAdapter(adapter);
            currentPage = 1;
        }else if(page == 2){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(notification,"Notification");
            home.onDestroy();
            place.onStop();
            notification.onStart();
            process.onStop();
            mViewPage.setAdapter(adapter);
            currentPage =2;
        }else if(page == 3){
            mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(process,"Progress");
            home.onStop();
            place.onStop();
            notification.onStop();
            process.onStart();
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

    protected static void bookmarkQueue(){
       // Log.d("bbbbbbbb","bbbbbbb start bookmark");
        int temproomid;
        CallApi getAppointment = new CallApi(patient.getId());
        getAppointment.execute("getAppointmentList");
        int tempAppointment = getAppointment.getAppointmentId();
        int tempEmployee = getAppointment.getEmployeeid();
        if(tempAppointment == -1){
                return;
        }else {
            if(queue == false && tempEmployee != 0){
                CallApi getRoomScheduleByPatientId =  new CallApi(tempEmployee);
                getRoomScheduleByPatientId.execute("getRoomScheduleByEmployeeId");
                temproomid = getRoomScheduleByPatientId.getRoomid();
                //Log.d("aaaaaaaaaaaa","aaaaa main room "+temproomid+"  queue T/F "+queue);
                if(temproomid != 0 && queue == false && tempAppointment != 0){
                    //Log.d("qqqqqqqqq","qqqqqqqq "+tempAppointment+" "+temproomid);
                    BookmarkQueue bookmarkQueue = new BookmarkQueue(patient.getId(),temproomid, tempAppointment);
                    if(tempconut == 0){
                        bookmarkQueue.execute("http://iopd.ml/?function=addQueue");
                        tempconut++;
                    }
                    queueNo = bookmarkQueue.getQueueNo();
                    //Log.d("fffffff","ffffffff "+queue+"    No "+queueNo);
                    if(queueNo != 0 && queue == false){
                       // Log.d("ffffffff","fffffffff  "+queueNo);
                        home.updateQueue(queueNo);
                        locationManager.removeUpdates(locationListener);
                        locationManager = null;
                        queue = true;
                    }
                }

            }
        }


    }

    public static boolean checkInArea(Location location){
        CallApi inArea = new CallApi(location.getLatitude(),location.getLongitude());
        inArea.execute("CheckInArea");
        Boolean temp = inArea.getInArea();
        return temp;
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

    protected static int getQueueNo(){
        return queueNo;
    }

    protected static String getState(){
        return stateDoing;
    }

    protected static String getTargetLocation(){
        return targetLocation;
    }

    protected static int getRemainQueue(){
        return remainQueue;
    }


}
