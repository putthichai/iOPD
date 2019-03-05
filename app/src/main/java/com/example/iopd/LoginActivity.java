package com.example.iopd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity implements iOPD{

    private  EditText usernameView,passwordView;
    private int countback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                pw = passwordView.getText().toString();

                startLogin(id,pw);
            }
        });

        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,Register.class);
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
    public void processFinish(Integer output) {
        if(output == 200){
            Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this,MainMenuActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "wrong username or password", Toast.LENGTH_SHORT).show();
        }

    }

    public void startLogin(String id, String pw){
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
            //Log.d("aaaaa","aaaaaa user "+id+"  pass "+pw);
            new LoginApi(LoginActivity.this,id,pw).execute("http://iopd.ml/?function=loginPatient");
        }

    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        if(username == ""){
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        if(password.length() < 4){
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
}
