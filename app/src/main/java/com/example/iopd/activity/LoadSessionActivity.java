package com.example.iopd.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.iopd.R;

public class LoadSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loadsession);


        Intent intent = new Intent(LoadSessionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


       // Intent intent = new Intent(LoadSessionActivity.this,MainMenuActivity.class);
       // startActivity(intent);
       // finish();
    }
}
