package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;

// TODO:
//  - UI
//  - Funktionalität
public class AppLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);
        initUI();

    }

    // dummy method to enable progression to other activities
    private void initUI() {
        Button toGoogleLogin = (Button) findViewById(R.id.login_to_google_login);
        Button toCalendar = (Button) findViewById(R.id.login_to_calendar);

        toGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleLoginActivity.class));
            }
        });

        toCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
            }
        });
    }
}
