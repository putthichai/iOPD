package com.example.iopd.api;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class getQueue extends AsyncTask<String,String, JSONObject> {

    private int patientId,workflowId;

    public getQueue(int patientId,int workflowId){
        this.patientId = patientId;
        this.workflowId = workflowId;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(strings[0]);
            String data = URLEncoder.encode("patientId", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(patientId), "UTF-8");

            data += "&" + URLEncoder.encode("workflowId", "UTF-8") + "="
                    + URLEncoder.encode(String.valueOf(workflowId), "UTF-8");

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

                sb.append(line + "\n");
            }

            conn.disconnect();
            wr.close();
            reader.close();
            jsonObject = new JSONObject(sb.toString());
            return jsonObject;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
