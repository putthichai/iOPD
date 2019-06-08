package com.example.iopd.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iopd.R;
import com.example.iopd.api.RegisterApi;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements iOPD2 {

    EditText nameView, sernameView, usernameView, passwordView, emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = findViewById(R.id.RegisterBTRe);
        Button login = findViewById(R.id.RegisterBTbtl);

        nameView = findViewById(R.id.RegisteerName);
        sernameView = findViewById(R.id.RegisterSurname);
        usernameView = findViewById(R.id.RegisterUsername);
        passwordView = findViewById(R.id.RegisterPassword);
        emailView = findViewById(R.id.RegisterEmail);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, surname, id, pw, email;
                name = nameView.getText().toString();
                surname = sernameView.getText().toString();
                id = usernameView.getText().toString();
                pw = passwordView.getText().toString();
                email = emailView.getText().toString();

                startRegister(name,surname,id,pw,email);

            }
        });

    }

    public void startRegister(String name, String surname,String id, String pw, String email){
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pw) && !isPasswordValid(pw)) {
            passwordView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordView;
            cancel = true;
        }
        if(TextUtils.isEmpty(pw)){
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(id)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }else if (!isUsernameValid(id)) {
            usernameView.setError(getString(R.string.error_invalid_username));
            focusView = usernameView;
            cancel = true;
        }

        // check for name, surname
        if (TextUtils.isEmpty(name)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(surname)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(email)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }else if (!isEmailValid(email)) {
            usernameView.setError(getString(R.string.error_incorrect_email));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            passwordView.setText("");
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            new RegisterApi(RegisterActivity.this,name,surname,id,pw,email).execute("https://iopdapi.ml/?function=registerPatient");
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isUsernameValid(String id){
        if(id.length() == 13 ){
            return true;
        }
        return false;
    }

    private  boolean isPasswordValid(String pw){
        if(pw.length() >= 8){
            return true;
        }
        return false;
    }

    @Override
    public void processFinish(JSONObject output) {
        try {
            if(output.getInt("status") == 200){
                Toast.makeText(this, "RegisterActivity success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, "Username already existed", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
