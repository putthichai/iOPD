package com.example.iopd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class QueueSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String QUEUE = "QUEUE";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String SURNAME = "SURNAME";
    public static final String ID = "ID";
    public static final String QUEUENO = "QUEUENO";

    public QueueSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(QUEUE, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(int queueNo){

        editor.putBoolean(QUEUE, true);
        editor.putInt(QUEUENO, queueNo);
        editor.apply();

    }

    public boolean isQueueNo(){
        return sharedPreferences.getBoolean(QUEUE, false);
    }



    public int getQueueDetail(){

        return sharedPreferences.getInt(QUEUENO,0);
    }

    public void clearSession(){

        editor.clear();
        editor.commit();

    }


}
