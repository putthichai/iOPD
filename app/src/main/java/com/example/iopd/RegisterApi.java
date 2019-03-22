package com.example.iopd;

import android.app.ProgressDialog;
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

public class RegisterApi extends AsyncTask<String, String, JSONObject> {
    private String username,password,name,surname,email;
    private Context mContext;
    ProgressDialog mProgress;
    private iOPD2 mCallback;


    public RegisterApi(Context context,String name,String surname,String id, String pw,String email){
        mContext = context;
        username = id;
        password = pw;
        mCallback = (iOPD2) context;
        this.name = name;
        this.surname = surname;
        this.email = email;

        Log.d("","111111111  create object");
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Please wait...");
        mProgress.show();
        Log.d("","111111111 onPreExecute");
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        Log.d("","111111111  start doInBackground");
        JSONObject jobj = null;
        int status = 0;
        try {
            String data = null;
            data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("firstname", "UTF-8") + "="
                    + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("surname", "UTF-8") + "="
                    + URLEncoder.encode(surname, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                    + URLEncoder.encode(email, "UTF-8");

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
            while((line = reader.readLine()) != null){
                //Append server response in string
                sb.append(line + "\n");
            }

            conn.disconnect();
            wr.close();
            reader.close();

            jobj = new JSONObject(sb.toString());
            status = jobj.getInt("status");
            Log.d("","111111111  end doInBackground");
            return jobj;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    @Override
    protected void onPostExecute(JSONObject integer) {
        //Log.d("","111111111  onPostExecute "+integer);
        mProgress.dismiss();
        mCallback.processFinish(integer);
    }
}
