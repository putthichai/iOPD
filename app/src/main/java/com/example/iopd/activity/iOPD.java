package com.example.iopd.activity;


import org.json.JSONArray;
import org.json.JSONObject;

public interface iOPD {


    void getIdRoom(int idroom);

    void bookmarkFinish(int queueNo);

    void checkIn(Boolean statue);

    void loadAllprocess(JSONObject jsonObject);

}
