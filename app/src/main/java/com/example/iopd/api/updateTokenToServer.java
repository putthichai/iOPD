package com.example.iopd.api;

import android.os.AsyncTask;

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

public class updateTokenToServer extends AsyncTask<String, Integer, Void> {

    private int patient_id;
    private String token;

    public updateTokenToServer(int id, String token){
        patient_id = id;
        this.token = token;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            String data = URLEncoder.encode("patient_id", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(patient_id), "UTF-8");

            data += "&" + URLEncoder.encode("token", "UTF-8") + "="
                    + URLEncoder.encode(token, "UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setReadTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                // Append server response in string

                sb.append(line + "\n");
            }

            conn.disconnect();
            wr.close();
            reader.close();
            return  null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
