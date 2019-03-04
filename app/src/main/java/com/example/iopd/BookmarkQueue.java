package com.example.iopd;


import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import junit.framework.Assert;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class BookmarkQueue extends AsyncTask<String, Void, Void> {

    private static  int patientId, roomId, appointmentId;

    private static int queueNo;

    public BookmarkQueue(int pId, int rId, int aId){
        patientId = pId;
        roomId = rId;
        appointmentId = aId;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            //Log.d(TAG,"222222222   roomId "+roomId+" appoint "+appointmentId);

            if(roomId == 0 || appointmentId == 0) return null;

           // Log.d(TAG,"222222222 pass   roomId "+roomId+" appoint "+appointmentId);
            String data = URLEncoder.encode("patient_id", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(patientId), "UTF-8");

            data += "&" + URLEncoder.encode("room_id", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(roomId), "UTF-8");

            data += "&" + URLEncoder.encode("appointment_id", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(appointmentId), "UTF-8");

            data += "&" + URLEncoder.encode("queue_type_id", "UTF-8")
                    + "=" + URLEncoder.encode("1", "UTF-8");

            data += "&" + URLEncoder.encode("queue_status_id", "UTF-8")
                    + "=" + URLEncoder.encode("1", "UTF-8");



            URL url = new URL(strings[0]);
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
                sb.append(line + "\n");
            }
            int tempsbatLast = sb.length();
            String temp = sb.toString().substring(24,tempsbatLast-2);

            conn.disconnect();
            reader.close();
            wr.close();

            JSONObject jobj = new JSONObject(temp);
            queueNo = jobj.getInt("queueNo");
           // Log.d("333333","3333333   "+queueNo);
            return null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }


    public int getQueueNo(){
        return queueNo;
    }
}


