package com.example.iopd;

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CallApi extends AsyncTask<String, Void, Void> {

    private URL url = null;
    private String function,list;
    private JSONObject jobj = null;
    private int size;
    private static int  appointmentid,employeeid,roomid,statusLogin;
    private int patientid;
    private static double longtitude,latitude;
    private static boolean inArea;

    public CallApi(int id){
        patientid = id;
    }

    public CallApi(double lati,double longti){
        latitude = lati;
        longtitude = longti;
    }

    @Override
    protected Void doInBackground(String... strings) {
        function = strings[0];
        StringBuffer buffer = new StringBuffer();
        try {
            url = new URL("http://iopd.ml/?function="+function);
            if(function == "getAppointmentList"){

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setDoInput(true);

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";

                while ((line = reader.readLine()) != null) {
                  buffer.append(line);
                }
                in.close();
                reader.close();
                conn.disconnect();

                jobj = new JSONObject(buffer.toString());
                size = jobj.getJSONArray("results").length();
                for(int i = 0; i < size;  i++){

                    if(jobj.getJSONArray("results").getJSONObject(i).getInt("patientId") == patientid){
                        Date current = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(current);
                        if(jobj.getJSONArray("results").getJSONObject(i).get("date").equals(formattedDate)){
                          appointmentid = jobj.getJSONArray("results").getJSONObject(i).getInt("id");
                          employeeid = jobj.getJSONArray("results").getJSONObject(i).getInt("employeeId");
                          return  null;
                        }else{
                          appointmentid = -1;
                          return null;
                        }
                    }
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
                int tempsbatLast = sb.length();
                String temp = sb.toString().substring(24,tempsbatLast-2);

                jobj = new JSONObject(temp);
                roomid = jobj.getInt("roomId");
                return null;
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
                    return null;
                }else{
                    inArea = false;
                    return null;
                }
            }else if(function == "loginPatient"){

                return null;
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
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

    public int getStatusLogin(){

        return statusLogin;
    }

}