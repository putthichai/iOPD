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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class ProcessApi extends AsyncTask<String, Integer, JSONObject> {

    private int queueNo,workflowId;
    private iOPD mCallback;


    public ProcessApi(int queueNo, int workflowId, Context context){
        mCallback = (iOPD) context;
        this.queueNo =queueNo;
        this.workflowId = workflowId;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String data = null;
        try {
            data = URLEncoder.encode("queueNo", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(queueNo), "UTF-8");
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

            conn.disconnect();
            reader.close();
            wr.close();
            JSONObject jobj = new JSONObject(sb.toString());
            //String status = getStatusQueue();

            //jobj.put("status",status);
            return jobj;
        } catch (UnsupportedEncodingException e) {
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
        //mCallback.loadProcess(object);
    }

    protected String getStatusQueue(){
        String data = null;
        try {
            data = URLEncoder.encode("queueNo", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(queueNo), "UTF-8");
            data += "&" + URLEncoder.encode("workflowId", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(workflowId), "UTF-8");
            URL url = new URL("https://iopdapi.ml/?function=getQueueByPatientId");
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

            conn.disconnect();
            reader.close();
            wr.close();
            JSONObject temp = new JSONObject(sb.toString());

            return temp.getJSONObject("results").getString("status_name");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
