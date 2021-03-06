package com.example.iopd.api;

import android.content.Context;
import android.os.AsyncTask;
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

public class AllProcessesApi extends AsyncTask<String, Integer, JSONObject> {

    private int workflowId,queueNo;
    private iOPD mCallback;

    public AllProcessesApi(int queueNo,int workflowId, Context context){
        this.workflowId = workflowId;
        this.queueNo = queueNo;
        mCallback = (iOPD) context;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            String data = URLEncoder.encode("queueNo", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(queueNo), "UTF-8");

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
            JSONObject jobj = new JSONObject(sb.toString());

            return  jobj;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
