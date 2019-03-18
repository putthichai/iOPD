package com.example.iopd;


import org.json.JSONArray;
import org.json.JSONObject;

public interface iOPD {

    void processFinish(JSONObject output);

    void getIdRoom(int idroom);

    void bookmarkFinish(int queueNo);

    void checkIn(Boolean statue);

    void loadProcess(JSONObject object);

    void loadAllprocess(JSONArray jsonArray);

}
