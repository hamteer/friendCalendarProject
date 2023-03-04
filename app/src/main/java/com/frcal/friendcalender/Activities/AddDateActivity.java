package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;

// TODO:
//  - UI
//  - Funktionalität
public class AddDateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs", MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean("fingerprintSwitchState", false);
        if (getIntent().hasExtra(getString(R.string.unregular_opening_key)) && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra("class", this.getClass().toString()));
        }
        setContentView(R.layout.activity_add_date);
    }
}
