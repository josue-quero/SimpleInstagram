package com.codepath.simpleinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class LaunchActivity extends AppCompatActivity {

    public static Activity launchActivity;

    public static final String TAG = "LaunchActivity";

    Button btnLoginOption;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        launchActivity = this;

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            goMainActivity();
        }

        btnSignIn = findViewById(R.id.btnSignIn);
        btnLoginOption = findViewById(R.id.btnLogOption);
        btnLoginOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                startLoginActivity();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                startSignUpActivity();
            }
        });

    }

    private void startSignUpActivity() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void goMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}