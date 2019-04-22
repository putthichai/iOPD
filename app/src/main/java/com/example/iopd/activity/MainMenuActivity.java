package com.example.iopd.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iopd.api.AllProcessesApi;
import com.example.iopd.api.AppointmentApi;
import com.example.iopd.api.BookmarkQueue;
import com.example.iopd.api.CallApi;
import com.example.iopd.api.CheckStatusInProcess;
import com.example.iopd.api.getQueue;
import com.example.iopd.api.updateTokenToServer;
import com.example.iopd.app.Config;
import com.example.iopd.app.Patient;
import com.example.iopd.api.ProcessApi;
import com.example.iopd.app.QueueSession;
import com.example.iopd.R;
import com.example.iopd.app.SectionsStatePagerAdapter;
import com.example.iopd.app.SessionManager;
import com.example.iopd.service.MyFirebaseInstanceIDService;
import com.example.iopd.service.SharedPrefManager;
import com.example.iopd.utils.NotificationUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MainMenuActivity extends AppCompatActivity implements iOPD {

    private static final String TAG = "";
    private static int queueNo;
    private ViewPager mViewPage;
    private static boolean queue;
    private String stateDoing, targetLocation, statusQueue;
    private int remainQueue, tempconut;
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
    private Boolean StatusGPS;
    SessionManager sessionManager;
    QueueSession queueSession;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String message,title,token;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_home:
                    setViewPager(0);
                    showToken();
                    return true;
                case R.id.action_notification:
                    setViewPager(2);
                    return true;
                case R.id.action_workflow:
                    setViewPager(3);
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

        //first request permission
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

        //load value from session
        sessionManager = new SessionManager(this);
        queueSession = new QueueSession(this);
        sessionManager.checkLogin();
        if(sessionManager.isLoggin()){

            //set up base value
            StatusGPS = true;
            queue = false;
            backButtonCount =0;
            currentPage =0;
            tempconut =0;
            countLocation =0;
            stateDoing = "";
            targetLocation = "";
            remainQueue = 0;
            message = "";
            title = "";

            //create notification
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                        displayFirebaseRegId();

                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        // new push notification is received
                        message = intent.getStringExtra("message");
                        title = intent.getStringExtra("title");

                        if(queueNo != 0){
                            setViewPager(2);
                            notification.setAdapterNotification(title,message);
                            checkStatusInProccess();
                        }

                    }
                }
            };

            if(queueSession.isQueueNo()){
            queueNo = queueSession.getQueueDetail();
            }else {
                queueNo = 0;
            }

            HashMap<String, String> user = sessionManager.getUserDetail();

            int tempid = Integer.valueOf(user.get(sessionManager.ID));
            String tempFN = user.get(sessionManager.NAME);
            String tempsur = user.get(sessionManager.SURNAME);

            patient = new Patient(tempid, tempFN, tempsur);



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

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            checkAppointment();
            checkQueue();
        }

        if(queueNo != 0){
            checkProcess();
        }

        turnOnGPS();

    }
    //check device token(debug)
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            token = regId;
        else
           token = "Firebase Reg Id is not received yet!";


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
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(home,"Home");
            currentPage = 0;
            mViewPage.setAdapter(adapter);
            checkAppointment();
            checkQueue();
            checkProcess();
            home.onStart();
            //home.setAppointment(patient.getAppointment());
            //home.setTime(patient.getTimeStart(),patient.getTimeEnd());


        }else if(page == 1){
            currentPage = 1;
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(place,"Suggestion location");
            mViewPage.setAdapter(adapter);
            checkProcess();
        }else if(page == 2){
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(notification,"Notification");
            mViewPage.setAdapter(adapter);
            currentPage =2;
        }else if(page == 3){
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
            currentPage = 5;
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(settingFragment,"Setting");
            mViewPage.setAdapter(adapter);
            settingFragment.checkStatusGPS(StatusGPS);
        }
    }

    protected void checkQueue() {
        JSONObject temp = null;
        int tempStatus = 400;
        int tempQueueNo = 0;
        String tempStatusQueue = "";
        try {
            temp = new getQueue(patient.getId(),patient.getWorkflowId()).execute("https://iopdapi.ml/?function=getQueueByPatientId").get();
            tempStatus = temp.getInt("status");
            if(tempStatus == 200){
                JSONObject temp2 = temp.getJSONObject("results");
                tempQueueNo = temp2.getInt("queueNo");
                tempStatusQueue = temp2.getString("status_name");
            }else{
                tempQueueNo = 0;
                tempStatusQueue = "-";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            tempQueueNo = 0;
            tempStatusQueue = "-";
        } catch (ExecutionException e) {
            e.printStackTrace();
            tempQueueNo = 0;
            tempStatusQueue = "-";
        } catch (JSONException e) {
            e.printStackTrace();
            tempQueueNo = 0;
            tempStatusQueue = "-";
        } finally {
                this.queueNo = tempQueueNo;
                statusQueue = tempStatusQueue;
                //home.updateStatus(tempStatusQueue);
               // home.updateQueue(this.queueNo);
        }
    }


    //set action back bottom
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
               // locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    protected static int getQueueNo(){
        return queueNo;
    }

    protected String getState(){
        return stateDoing;
    }

    protected String getTargetLocation(){
        return targetLocation;
    }

    protected int getRemainQueue(){
        return remainQueue;
    }

    protected static Patient getPatient(){
        return patient;
    }
    protected String getStatusQueue(){
        return statusQueue;
    }


    @Override
    public void processFinish(JSONObject output) {


    }

    @Override
    public void getIdRoom(final int idRoom) {
        LocalTime currenttime = LocalTime.now();
        String[] tempStart = patient.getTimeStart().split(":");
        int tempHourStart = Integer.parseInt(tempStart[0]);
        int tempMinStart = Integer.parseInt(tempStart[1]);
        int tempSecStart = Integer.parseInt(tempStart[2]);
        String[] tempEnd = patient.getTimeEnd().split(":");
        int tempHourEnd = Integer.parseInt(tempEnd[0]);
        int tempMinEnd = Integer.parseInt(tempEnd[1]);
        int tempSecend = Integer.parseInt(tempEnd[2]);
        LocalTime timeStar = LocalTime.of(tempHourStart,tempMinStart,tempSecStart);
        LocalTime timeEnd = LocalTime.of(tempHourEnd,tempMinEnd,tempSecend);
        if(currenttime.isAfter(timeStar) && currenttime.isBefore(timeEnd)){
            if(tempconut == 0){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(MainMenuActivity.this);
                builder.setMessage("คุณต้องการจองคิวมั้ยคะ?");
                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new BookmarkQueue(patient.getId(),idRoom,patient.getAppointmentId(),patient.getWorkflowId(),MainMenuActivity.this).execute("https://iopdapi.ml/?function=addQueue");
                        Toast.makeText(getApplicationContext(), "คุณมีนัดใช่มั้ย", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        turnOffGPS();
                        StatusGPS = false;
                        countLocation = 0;
                        tempconut = 0;
                    }
                });
                builder.show();
                tempconut++;
            }
        }else{
            Toast.makeText(getApplicationContext(),"Please booking in the appointment time", Toast.LENGTH_LONG).show();
        }

    }

    protected void setStatusGPS(boolean b){
        StatusGPS = b;
    }

    public Boolean getStatusGPS(){
        return StatusGPS;
    }

    @Override
    public void bookmarkFinish(int queueNo) {
        this.queueNo = queueNo;
        queueSession.createSession(queueNo);
        home.updateQueue(queueNo);
        queue = true;
        checkProcess();

    }

    @Override
    public void checkIn(Boolean statue) {
        if(statue && haveNetwork()){
            bookmarkQueue();
        }else if(!haveNetwork()){
            Toast.makeText(getApplicationContext(),"Network connection is not avalilable",Toast.LENGTH_SHORT).show();
            countLocation = 0;
        }else if(!statue){
            Toast.makeText(getApplicationContext(),"Out of reach",Toast.LENGTH_SHORT).show();
            countLocation = 0;
        }
    }

    @Override
    public void loadProcess(JSONObject object) {
        /*try {
            if(object != null){
                Log.d("aaaaaaaaaaa",object.toString());
                if(object.getInt("status") == 200){
                    if(currentPage == 0){
                        stateDoing = object.getJSONObject("results").getString("step");
                        targetLocation = object.getJSONObject("results").getString("targetPlace");
                        remainQueue = object.getJSONObject("results").getInt("remainQueue");
                        home.changeState(stateDoing,targetLocation,remainQueue);
                    }
                    else if(currentPage == 1){
                        place.setRemainQueue(object.getJSONObject("results").getInt("remainQueue"));
                    }
                }
                else {
                    statusQueue = "-";
                    stateDoing = "-";
                    targetLocation = "-";
                    remainQueue = 0;
                    home.onStart();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void loadAllprocess(JSONObject jsonObject) {
            try {

                if(jsonObject.getInt("status") == 200){
                    int tempNum = jsonObject.getJSONArray("results").length();
                    String[] name = new String[tempNum];
                    int[] id = new int[tempNum];
                    for(int i=0; i<tempNum;i++){
                        JSONObject temp = jsonObject.getJSONArray("results").getJSONObject(i);
                        name[i] = temp.getString("name");
                        id[i] = jsonObject.getJSONArray("state").getInt(i);
                    }
                    process.setProcess(name,id);
                }


            } catch (JSONException e) {
                e.printStackTrace();
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
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
        queueSession.clearSession();
        sessionManager.logout();
        SharedPrefManager.getInstance(this).deleteDeviceToken();
        new updateTokenToServer(patient.getId()," ").execute("https://iopdapi.ml/?function=updatePatientToken");
    }

    public void checkProcess(){
        JSONObject temp = null;
        try {
            temp = new ProcessApi(queueNo,patient.getWorkflowId(),MainMenuActivity.this).execute("https://iopdapi.ml/?function=getStep").get();
            Log.d("aaaaaaaaaaaaaa",temp.toString());
            stateDoing = temp.getJSONObject("results").getString("step");
            targetLocation = temp.getJSONObject("results").getString("targetPlace");
            remainQueue = temp.getJSONObject("results").getInt("remainQueue");
        } catch (InterruptedException e) {
            e.printStackTrace();
            stateDoing = "";
            targetLocation = "";
            remainQueue = 0;
        } catch (ExecutionException e) {
            e.printStackTrace();
            stateDoing = "";
            targetLocation = "";
            remainQueue = 0;
        } catch (JSONException e) {
            e.printStackTrace();
            stateDoing = "";
            targetLocation = "";
            remainQueue = 0;
        }
    }

    public void turnOnGPS(){
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
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.d("cccccccccccccccc","bbbbbbb in onLocationChanged function");
                if(countLocation == 0 && queueNo == 0 && isInternetConnection()){
                    //Log.d("cccccccccccccccc","bbbbbbb start to check in area");
                    new CallApi(location.getLatitude(),location.getLongitude(),MainMenuActivity.this).execute("CheckInArea");
                    countLocation++;
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


        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 10000, 0, locationListener);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 10000, 0, locationListener);

    }

    public void turnOffGPS(){

        if(currentPage == 5){
            settingFragment.checkStatusGPS(false);
        }

        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }

    public void callAllProcess(){
       if(isInternetConnection())
        new AllProcessesApi(queueNo,patient.getWorkflowId(),MainMenuActivity.this).execute("https://iopdapi.ml/?function=checkStateInProcess");
    }

    public void checkAppointment(){
        JSONObject temp = null;
        int tempStatus = 400;
        String date = "";
        String processName = "";
        try {
            temp = new AppointmentApi(MainMenuActivity.this,patient.getId()).execute("https://iopdapi.ml/?function=getAppointmentByPatientsId").get();
            Log.d("aaaaaaaaaaaaaa",temp.toString());
            tempStatus = temp.getInt("status");
            if(tempStatus == 200){
                String[] tempDate = temp.getJSONObject("results").getString("date").split("-");
                date = tempDate[2]+"-"+tempDate[1]+"-"+tempDate[0];
                patient.setAppointmentDate(date);
                patient.setAppointment(temp.getJSONObject("results").getInt("employeeId"),temp.getJSONObject("results").getInt("id"));
                patient.setTime(temp.getJSONObject("results").getString("timeslot_starttime"),temp.getJSONObject("results").getString("timeslot_endtime"));
                patient.setWorkflowId(temp.getJSONObject("results").getInt("workflowId"));
                processName = temp.getString("process");

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            Log.d("aaaaaaaaaaaaaaaaaa","status "+tempStatus);
            if(temp != null && tempStatus == 200){
                Log.d("aaaaaaaaaaaaaaa",date+"     "+patient.toString()+"       "+home);
                right.setText(processName);
            }

        }
    }

    public void finishProcess(){
            turnOffGPS();
            checkAppointment();
        queueNo = 0;
        home.updateQueue(queueNo);
        queueSession.clearSession();
        queue = false;
        stateDoing = "";
        targetLocation = "";
        remainQueue = 0;
        home.changeState("-","-",0);
    }

    public void checkStatusInProccess(){
        Boolean status = false;
        try {

            status = new CheckStatusInProcess(queueNo).execute("https://iopdapi.ml/?function=checkStatusInProcess").get();
            if(status == false){
                finishProcess();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public String getMessage(){
        return message;
    }

    public String getSubject(){
        return title;
    }

    public void showToken(){

        token = SharedPrefManager.getInstance(this).getDeviceToken();

        //if token is not null
        if (token != null) {
            //displaying the token
            Log.d("555555555555555555555",token);
        } else {
            //if token is null that means something wrong
            Log.d("555555555555555555555","Token not generated");
        }

    }

    public  boolean isInternetConnection()
    {

        ConnectivityManager connectivityManager =  (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }






}
