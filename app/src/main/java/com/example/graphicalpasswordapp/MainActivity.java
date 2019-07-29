package com.example.graphicalpasswordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
        Intent goToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(goToLoginActivity);
    }

    public void signUp(View view){
        Intent goToSignUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(goToSignUpActivity);
    }
}
