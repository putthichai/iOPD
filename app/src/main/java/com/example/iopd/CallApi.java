package com.example.iopd;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import static androidx.constraintlayout.widget.Constraints.TAG;


public class CallApi extends AsyncTask<String, Void, String> {

    private URL url = null;
    private String function,list;
    protected JSONObject jobj = null;
    private int size;
    private static int patientid, appointmentid,employeeid;

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
                     // Log.d(TAG,"aaaaaa in case" +jobj.getJSONArray("results").getJSONObject(i).get("date")+"    "+formattedDate);
                      if(jobj.getJSONArray("results").getJSONObject(i).get("date").equals(formattedDate)){
                          appointmentid = jobj.getJSONArray("results").getJSONObject(i).getInt("id");
                          employeeid = jobj.getJSONArray("results").getJSONObject(i).getInt("employeeId");
                          Log.d(TAG,"aaaaaa   "+appointmentid+"      "+employeeid);
                      }else{
                          appointmentid = -1;
                      }
                  }
              }

          }else if(function == "getRoomScheduleByPatientId"){
              HttpURLConnection conn = (HttpURLConnection)url.openConnection();


          }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


       //Log.d(TAG, "doInBackground:aaaaa disconnection2"+ jobj.toString());

        list = buffer.toString();
        return list;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);

    }

    protected int getAppointmentId(){
        return appointmentid;
    }

}
