package com.example.iopd;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


import static androidx.constraintlayout.widget.Constraints.TAG;


public class CallApi extends AsyncTask<String, Void, String> {

    private URL url = null;
    private String function,list;
    private JSONObject jobj = null;
    private int size;
    private static int  appointmentid,employeeid,roomid;
    private int patientid;

    public CallApi(int id){
        patientid = id;
    }

    @Override
    protected String doInBackground(String... strings) {
        function = strings[0];
        StringBuffer buffer = new StringBuffer();
        try {
            url = new URL("http://iopd.ml/?function="+function);
          if(function == "getAppointmentList"){

              //Log.d(TAG, "doInBackground:aaaaa start connection");
              HttpURLConnection conn = (HttpURLConnection)url.openConnection();
              conn.setRequestMethod("GET");
              conn.setReadTimeout(10000);
              conn.setReadTimeout(15000);
              conn.setDoInput(true);

              InputStream in = conn.getInputStream();
              BufferedReader reader = new BufferedReader(new InputStreamReader(in));

             // Log.d(TAG, "doInBackground:aaaaaa "+reader.toString());

              String line = "";
              //File file = new File("storage/sdcard/MyIdea/MyCompositions/" + "temp" + ".json");
              while ((line = reader.readLine()) != null) {
                  //Log.d("bbbbbb",line);
                  buffer.append(line);
              }
             // Log.d(TAG, "doInBackground:aaaaaa "+buffer.toString());
              in.close();
              reader.close();
              conn.disconnect();
              //Log.d(TAG, "doInBackground:aaaaa disconnection");
              jobj = new JSONObject(buffer.toString());
              size = jobj.getJSONArray("results").length();
             // Log.d(TAG, "doInBackground:aaaaa test Json  no"+size);

             for(int i = 0; i < size;  i++){
                 // Log.d(TAG, "doInBackground:aaaaa test Json"+jobj.getJSONArray("results").getJSONObject(i).getInt("patientId"));
                  if(jobj.getJSONArray("results").getJSONObject(i).getInt("patientId") == patientid){
                     // Log.d(TAG,"aaaaaa in case" +i);

                      Date current = Calendar.getInstance().getTime();
                      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                      String formattedDate = df.format(current);
                     //Log.d(TAG,"aaaaaa in cause" +jobj.getJSONArray("results").getJSONObject(i).toString());
                      if(jobj.getJSONArray("results").getJSONObject(i).get("date").equals(formattedDate)){
                          appointmentid = jobj.getJSONArray("results").getJSONObject(i).getInt("id");
                          employeeid = jobj.getJSONArray("results").getJSONObject(i).getInt("employeeId");
                         Log.d(TAG,"aaaaaa   app"+appointmentid+"      em"+employeeid);
                      }else{
                          appointmentid = -1;
                      }
                  }
              }

          }else if(function == "getRoomScheduleByEmployeeId"){

              Log.d(TAG,"aaaaaa Room employee "+patientid);

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
              while((line = reader.readLine()) != null)
              {
                  // Append server response in string
                  Log.d(TAG,"bbbbbb loop "+line+"\n");
                  sb.append(line + "\n");
              }

              conn.disconnect();
              wr.close();
              reader.close();
              int tempsbatLast = sb.length();
              String temp = sb.toString().substring(24,tempsbatLast-2);

              //Log.d(ContentValues.TAG,"ssssssss string "+temp);
              jobj = new JSONObject(temp);
              //Log.d("sssssss","ssssss json create"+jobj.toString());
              //Log.d(ContentValues.TAG,"aaaaaaa room size"+jobj.getInt("roomId"));
              //size = jobj.getJSONArray("results").length();
              roomid = jobj.getInt("roomId");
             // Log.d("ssssss room ","ssssss room check size"+jobj.getInt("roomId"));

            /*  HttpURLConnection conn = (HttpURLConnection)url.openConnection();
              conn.setRequestMethod("GET");
              conn.setReadTimeout(10000);
              conn.setReadTimeout(15000);
              conn.setDoInput(true);

              InputStream in = conn.getInputStream();
              BufferedReader reader = new BufferedReader(new InputStreamReader(in));
              String line = "";
              while ((line = reader.readLine()) != null) {
                  //Log.d("bbbbbb",line);
                  buffer.append(line);
              }
              Log.d(TAG, "doInBackground:aaaaaa "+buffer.toString());
              in.close();
              reader.close();
              conn.disconnect();

              jobj = new JSONObject(buffer.toString());
              size = jobj.getJSONArray("results").length();
                Log.d("aaaaaaaaa room ","aaaaaaaaa room "+size);
              for(int i = 0; i < size;  i++){
                  Log.d(TAG, "doInBackground:aaaaa test Json"+jobj.getJSONArray("results").getJSONObject(i).getInt("patientId"));

                  if(jobj.getJSONArray("results").getJSONObject(i).getInt("doctor_id") == patientid){
                        roomid = jobj.getJSONArray("results").getJSONObject(i).getInt("place_id");
                        Log.d("aaaaaaa roomId ","aaaaaaa roomId "+roomid);
                  }

              }
*/

          }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


       //Log.d(TAG, "doInBackground:aaaaa disconnection2"+ jobj.toString());

        return null;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);

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

}
