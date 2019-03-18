package com.example.iopd;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainMenuActivity extends AppCompatActivity implements iOPD{

    private static final String TAG = "";
    private static int queueNo;
    private ViewPager mViewPage;
    private static boolean queue;
    private static String stateDoing, targetLocation;
    private static int remainQueue, tempconut;
    private FirebaseInstanceIdService firebaseInstanceIdService;
    private int backButtonCount,countLocation;
    private int currentPage;
    private TextView right ,fullname;
    private static Patient patient;
    protected static LocationListener locationListener;
    protected static LocationManager locationManager;
    private static HomeFragment home;
    private NotificationFragment notification;
    private ProcessFragment process;
    private PlaceFragment place;
    private SettingFragment settingFragment;
    SessionManager sessionManager;
    QueueSession queueSession;

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

        sessionManager = new SessionManager(this);
        queueSession = new QueueSession(this);
        sessionManager.checkLogin();
        if(sessionManager.isLoggin()){
            Log.d("ssssssssssssssss",""+queueSession.isQueueNo());

            if(queueSession.isQueueNo()){
            queueNo = queueSession.getQueueDetail();
            }else {
                queueNo = 0;
            }


            //create patient
            //Intent tempIntent = getIntent();
            //Bundle bundle = tempIntent.getExtras();
            HashMap<String, String> user = sessionManager.getUserDetail();

            int tempid = Integer.valueOf(user.get(sessionManager.ID));
            String tempFN = user.get(sessionManager.NAME);
            String tempsur = user.get(sessionManager.SURNAME);

            //int tempid = bundle.getInt("id");
            //String tempFN = bundle.getString("firstname");
            //String tempsur = bundle.getString("surname");
            patient = new Patient(tempid, tempFN, tempsur);

            //set area of the hospital
            queue = false;
            backButtonCount =0;
            currentPage =0;
            tempconut =0;
            countLocation =0;

            //
            stateDoing = "";
            targetLocation = "";
            remainQueue = 0;

            //setup page
            mViewPage = findViewById(R.id.fragment);

            home = new HomeFragment();
            notification = new NotificationFragment();
            process = new ProcessFragment();
            place = new PlaceFragment();
            settingFragment = new SettingFragment();

            //link with mainmenu
            setupViewPager(mViewPage);
            right = findViewById(R.id.right);
            fullname = findViewById(R.id.name);
            fullname.setText(patient.getFullname());

            new AppointmentApi(MainMenuActivity.this,patient.getId()).execute("https://iopdapi.ml/?function=getAppointmentByPatientsId");

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            firebaseInstanceIdService = new FirebaseInstanceIdService();

            turnOnGPS();

        }

        if(queueNo != 0){
            checkProcess();
        }

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
            //mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(home,"Home");
            currentPage = 0;
            mViewPage.setAdapter(adapter);
            home.setAppointment(patient.getAppointment());
            home.setTime(patient.getTimeStart(),patient.getTimeEnd());
            checkProcess();
        }else if(page == 1){
            //mViewPage.removeAllViews();
            currentPage = 1;
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(place,"Suggestion location");
            mViewPage.setAdapter(adapter);
            checkProcess();
        }else if(page == 2){
            //mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(notification,"Notification");
            mViewPage.setAdapter(adapter);
            currentPage =2;
        }else if(page == 3){
            //mViewPage.removeAllViews();
            currentPage = 3;
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(process,"Progress");
            mViewPage.setAdapter(adapter);
            callAllProcess();
        }else if(page == 4){
            //mViewPage.removeAllViews();
            //SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            //adapter.addFragment(new Place2Fragment(),"Progress");
            //mViewPage.setAdapter(adapter);
            //currentPage = 4;
        }else if(page == 5){
            //mViewPage.removeAllViews();
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(settingFragment,"Setting");
            mViewPage.setAdapter(adapter);
            currentPage = 5;
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
        }else  if(currentPage == 5){
            setViewPager(0);
            backButtonCount =0;
        }
    }

    protected void bookmarkQueue(){
        if(patient.haveAppointment()){
            new CallApi(patient.getDoctor(),MainMenuActivity.this).execute("getRoomScheduleByEmployeeId");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
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

    protected static Patient getPatient(){
        return patient;
    }


    @Override
    public void processFinish(JSONObject output) {

        try {
            String[] tempDate = output.getJSONObject("results").getString("date").split("-");
            String date = tempDate[2]+"-"+tempDate[1]+"-"+tempDate[0];
            patient.setAppointmentDate(date);
            patient.setAppointment(output.getJSONObject("results").getInt("employeeId"),output.getJSONObject("results").getInt("id"));
            home.setAppointment(date);
            patient.setTime(output.getJSONObject("results").getString("timeslot_starttime"),output.getJSONObject("results").getString("timeslot_endtime"));
            home.setTime(patient.getTimeStart(),patient.getTimeEnd());
            patient.setWorkflowId(output.getJSONObject("results").getInt("workflowId"));
            right.setText(output.getString("process"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getIdRoom(final int idRoom) {
        if(tempconut == 0){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainMenuActivity.this);
            builder.setMessage("คุณต้องการจองคิวมั้ยคะ?");
            builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new BookmarkQueue(patient.getId(),idRoom,patient.getAppointmentId(),patient.getWorkflowId(),MainMenuActivity.this).execute("https://iopdapi.ml/?function=addQueue");
                    Toast.makeText(getApplicationContext(), "คุณได้ทำการจองคิวแล้วคะ", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.dismiss();
                }
            });
            builder.show();
            tempconut++;
        }

    }

    @Override
    public void bookmarkFinish(int queueNo) {
        //Log.d("cccccccccccccccc","bbbbbbb end to bookmark");
        this.queueNo = queueNo;
        queueSession.createSession(queueNo);
        home.updateQueue(queueNo);
        queue = true;
        checkProcess();

    }

    @Override
    public void checkIn(Boolean statue) {
        //Toast.makeText(getApplicationContext(),"Network connection is not avalilable "+haveNetwork()+"   "+statue,Toast.LENGTH_SHORT).show();
        if(statue && haveNetwork()){
            //Log.d("cccccccccccccccc","bbbbbbb In area");
            bookmarkQueue();
        }else if(!haveNetwork()){
            //Log.d("cccccccccccccccc","bbbbbbb not In area");
            Toast.makeText(getApplicationContext(),"Network connection is not avalilable",Toast.LENGTH_SHORT).show();
            countLocation = 0;
            //locationManager.removeUpdates(locationListener);
            //locationManager = null;
        }else if(!statue){
            Toast.makeText(getApplicationContext(),"Out of reach",Toast.LENGTH_SHORT).show();
            countLocation = 0;
        }
    }

    @Override
    public void loadProcess(JSONObject object) {
        try {
            if(object != null){
                Log.d("vvvvvvvvvvvvvvvv","loadProcess");
                if(currentPage == 0){
                    home.changeState(object.getString("step"),object.getString("targetPlace"),object.getInt("remainQueue"));
                }
                else {
                    place.setRemainQueue(object.getInt("remainQueue"));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAllprocess(JSONArray jsonArray) {
        Log.d("222222222222","load value all process");

        if(jsonArray != null){
            String[] name = new String[jsonArray.length()];
            int[] id = new int[jsonArray.length()];
            try {
                for(int i=0; i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);
                    name[i] = temp.getString("name");
                    id[i] =(temp.getInt("queueTypeId"));
                }
                process.setProcess(name,id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //process.setProcess();
    }

    private boolean haveNetwork(){
        boolean have_WIFI = false;
        boolean have_MOBILEDATA = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI"));
            if(info.isConnected())
                have_WIFI = true;

            if(info.getTypeName().equalsIgnoreCase("MOBILE"));
            if(info.isConnected())
                have_MOBILEDATA = true;

        }

        return have_MOBILEDATA || have_WIFI;
    }

    public void logout(){
        //queueSession.clearSession();
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        queueSession.clearSession();
        sessionManager.logout();

    }

    public void checkProcess(){
        new ProcessApi(queueNo,MainMenuActivity.this).execute("https://iopdapi.ml/?function=getStep");
    }

    public void turnOnGPS(){

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.d("cccccccccccccccc","bbbbbbb in onLocationChanged function");
                if(countLocation == 0 && queueNo == 0){
                    //Log.d("cccccccccccccccc","bbbbbbb start to check in area");
                    new CallApi(location.getLatitude(),location.getLongitude(),MainMenuActivity.this).execute("CheckInArea");
                    countLocation++;
                }
                if(queueNo != 0){
                    //checkProcess();
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
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

    }

    public void turnOffGPS(){
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }

    public void callAllProcess(){
        Log.d("2222222222","callAllProcesses");
        new AllProcessesApi(patient.getWorkflowId(),MainMenuActivity.this).execute("https://iopdapi.ml/?function=getAllProcesses");
    }
}
