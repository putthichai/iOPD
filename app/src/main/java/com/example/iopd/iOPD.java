package com.example.iopd;


import org.json.JSONObject;

public interface iOPD {

    void processFinish(JSONObject output);

    void getIdRoom(int idroom);

    void bookmarkFinish(int queueNo);

}
