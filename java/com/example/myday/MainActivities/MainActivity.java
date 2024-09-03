package com.example.myday.MainActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.example.myday.Activities.HomeActivity;
import com.example.myday.Activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthUser currentUser = Amplify.Auth.getCurrentUser();
        Intent intent;
        if (currentUser == null) {
            // Go to login screen
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        } else {
            // Go to home screen
            intent = new Intent(getApplicationContext(), HomeActivity.class);
        }
        // Start activity
        startActivity(intent);
        finish();
    }
}