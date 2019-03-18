package com.example.iopd;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ProcessApi extends AsyncTask<String, Integer, JSONObject> {

    private int queueNo;
    private iOPD mCallback;


    public ProcessApi(int queueNo, Context context){
        mCallback = (iOPD) context;
        this.queueNo =queueNo;
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
            Log.d("vvvvvvvv",jobj.toString());
            return jobj.getJSONObject("results");
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
        mCallback.loadProcess(object);
    }
}
