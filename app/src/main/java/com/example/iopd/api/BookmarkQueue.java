package com.example.iopd.api;


import android.content.Context;
import android.os.AsyncTask;
import com.example.iopd.activity.iOPD;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BookmarkQueue extends AsyncTask<String, Void, JSONObject> {

    private static  int patientId, roomId, appointmentId,workflowId;

    private iOPD mCallback;

    public BookmarkQueue(int pId, int rId, int aId,int wId, Context context){
        patientId = pId;
        roomId = rId;
        appointmentId = aId;
        workflowId = wId;
        mCallback = (iOPD) context;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            if(roomId == 0 || appointmentId == 0) return null;
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

            data += "&" + URLEncoder.encode("workflowId", "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(workflowId), "UTF-8");



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
            return jobj;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        try {
            mCallback.bookmarkFinish(object.getJSONObject("results").getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


