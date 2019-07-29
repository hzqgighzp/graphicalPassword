package com.example.graphicalpasswordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
    }

    public void home(View view){
        startActivity(new Intent(SuccessActivity.this, MainActivity.class));
    }

    public void backToLogin(View view){
        startActivity(new Intent(SuccessActivity.this, LoginActivity.class));
    }
}
