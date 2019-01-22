package com.example.iopd;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

class JobServices extends JobService {

    private static final String TAG = "JobServices";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing Long running task in schedule job");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
