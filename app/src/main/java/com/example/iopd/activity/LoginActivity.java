package com.example.iopd.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iopd.api.LoginApi;
import com.example.iopd.R;
import com.example.iopd.api.updateTokenToServer;
import com.example.iopd.app.SessionManager;
import com.example.iopd.service.MyFirebaseInstanceIDService;
import com.example.iopd.service.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements iOPD2 {

    private  EditText usernameView,passwordView;
    private int countback, pateintId;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        countback = 0;
        usernameView = findViewById(R.id.loginUsername);
        passwordView = findViewById(R.id.loginPassword);
        Button confirmView = findViewById(R.id.confirm);
        Button registerView = findViewById(R.id.register);
        Button resetView = findViewById(R.id.reset);


            confirmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id,pw;
                    id = usernameView.getText().toString();
                    pw = passwordView.getText().toString();if(haveNetwork()){
                    startLogin(id,pw);
                }else if(!haveNetwork()){
                    Toast.makeText(LoginActivity.this,"Network connection is not avalilable",Toast.LENGTH_SHORT).show();
                }
                }
            });



        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameView.setText("");
                passwordView.setText("");
            }
        });

    }


    @Override
    public void processFinish(JSONObject output) {
        int tempStatus = 0,tempId = 0;
        String tempFirst = "", tempSur = "";

        if(output != null){
            try {
                tempStatus = output.getInt("status");
                tempId = output.getJSONObject("results").getInt("user_id");
                tempFirst = output.getJSONObject("results").getString("firstname");
                tempSur = output.getJSONObject("results").getString("surname");
            } catch (JSONException e) {
                e.printStackTrace();

            }

            if(tempStatus == 200){
                sessionManager.createLoginSession(tempFirst,tempSur,String.valueOf(tempId));
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);
                new updateTokenToServer(tempId,refreshedToken).execute("https://iopdapi.ml/?function=updatePatientToken");
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "username or password is wrong", Toast.LENGTH_SHORT).show();
                passwordView.setText("");
            }
        }else {
                Toast.makeText(this, "check internet", Toast.LENGTH_SHORT).show();
                passwordView.setText("");
        }


    }

    public void startLogin(String id, String pw){
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(pw)){
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(id)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }else if (!isUsernameValid(id)) {
            usernameView.setError(getString(R.string.error_invalid_username));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            new LoginApi(LoginActivity.this,id,pw).execute("https://iopdapi.ml/?function=loginPatient");
        }

    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        if(username == ""){
            return false;
        }
        return true;
    }

    public void onBackPressed()
    {
        if(countback == 0){
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
        }
        if(countback > 1){
            finish();
        }
        countback++;
    }

    private boolean haveNetwork(){
        boolean have_WIFI = false;
        boolean have_MOBILEDATA = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI"));
                if(info.isConnected())
                    have_WIFI = true;

            if(info.getTypeName().equalsIgnoreCase("MOBILE"));
                if(info.isConnected())
                    have_MOBILEDATA = true;

        }

        return have_MOBILEDATA || have_WIFI;
    }
}
