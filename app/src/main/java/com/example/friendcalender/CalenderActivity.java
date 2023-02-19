package com.example.friendcalender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class CalenderActivity extends AppCompatActivity {

    private ImageView viewSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        viewSettings= findViewById(R.id.viewSettings);
        viewSettings.setOnClickListener(view -> {
            startActivity(new Intent(CalenderActivity.this, SettingsActivity.class));
        });
    }
}