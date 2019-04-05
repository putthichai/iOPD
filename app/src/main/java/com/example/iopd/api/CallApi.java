package com.example.iopd.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.iopd.activity.iOPD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CallApi extends AsyncTask<String, Void, JSONObject> {

    private URL url = null;
    private String function,list;
    private JSONObject jobj = null;
    private int size;
    private static int  appointmentid,employeeid,roomid,statusLogin;
    private int patientid;
    private static double longtitude,latitude;
    private static boolean inArea;
    private iOPD mCallback;

    public CallApi(int id, Context context){
        patientid = id;
        mCallback = (iOPD) context;
    }

    public CallApi(double lati,double longti, Context context){
        latitude = lati;
        longtitude = longti;
        mCallback = (iOPD) context;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        function = strings[0];
        StringBuffer buffer = new StringBuffer();
        try {
            url = new URL("https://iopdapi.ml/?function="+function);
            if(function == "getAppointmentByPatientsId"){

                String data = URLEncoder.encode("patient_id", "UTF-8")
                        + "=" + URLEncoder.encode(String.valueOf(patientid), "UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    // Append server response in string
                    // Log.d(TAG,"bbbbbb loop "+line+"\n");
                    sb.append(line + "\n");
                }

                conn.disconnect();
                wr.close();
                reader.close();

                jobj = new JSONObject(sb.toString());
                Date current = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(current);
                if(jobj.getJSONObject("results").getString("date").equals(formattedDate)){
                    appointmentid = jobj.getJSONObject("results").getInt("id");
                    employeeid = jobj.getJSONObject("results").getInt("employeeId");
                    Log.d(TAG,"asasas  "+appointmentid+"    "+employeeid);
                    return  jobj;
                }else{
                    appointmentid = -1;
                    return jobj;
                }
            }else if(function == "getRoomScheduleByEmployeeId"){

                String data = URLEncoder.encode("employee_id", "UTF-8")
                      + "=" + URLEncoder.encode(String.valueOf(patientid), "UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

              // Read Server Response
                while((line = reader.readLine()) != null) {
                  // Append server response in string
                 // Log.d(TAG,"bbbbbb loop "+line+"\n");
                  sb.append(line + "\n");
                }

                conn.disconnect();
                wr.close();
                reader.close();

                jobj = new JSONObject(sb.toString());
                roomid = jobj.getJSONObject("results").getInt("roomId");

                return jobj;
            }else if(function == "CheckInArea"){

             // Log.d(TAG,"lllllll strat check in area la"+latitude+"   long"+longtitude);

                String data = URLEncoder.encode("latpatient", "UTF-8")
                      + "=" + URLEncoder.encode(String.valueOf(latitude), "UTF-8");

                data += "&" + URLEncoder.encode("longpatient", "UTF-8") + "="
                      + URLEncoder.encode(String.valueOf(longtitude), "UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null){
                  //Append server response in string
                    sb.append(line + "\n");
                }

                conn.disconnect();
                wr.close();
                reader.close();

                jobj = new JSONObject(sb.toString());
                if(jobj.getInt("result") == 1){
                    inArea = true;
                }else{
                    inArea = false;
                }

                return jobj;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        Log.d("cccccccccccccccc","bbbbbbb End CallApi function "+function);
       if(function == "getRoomScheduleByEmployeeId"){
           try {
               mCallback.getIdRoom(object.getJSONObject("results").getInt("roomId"));
           } catch (JSONException e) {
               e.printStackTrace();
           }finally {
               mCallback.getIdRoom(0);
           }
       }else if(function == "CheckInArea"){
           Boolean temp = false;
           try {
               if(object.getInt("result") == 1){
                   temp = true;
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }finally {
               mCallback.checkIn(temp);
           }

       }

    }


    public int getAppointmentId(){
        return appointmentid;
    }

    public int getEmployeeid(){
        return employeeid;
    }

    public int getRoomid(){
        return roomid;
    }

    public boolean getInArea(){
        return inArea;
    }

}