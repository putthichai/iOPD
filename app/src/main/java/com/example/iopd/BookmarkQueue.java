package com.example.iopd;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class BookmarkQueue extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public BookmarkQueue(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        GPSTracker gps = new GPSTracker(mContext);

        return null;
    }
}


