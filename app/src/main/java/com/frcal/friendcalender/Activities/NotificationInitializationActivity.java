package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationInitializationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs",
                MODE_PRIVATE);
        boolean notificationsAllowed = sharedPreferences.getBoolean("notificationsAllowed", false);
        if (!notificationsAllowed) {
            sharedPreferences.edit().putBoolean("notificationsAllowed", true).apply();
        }
        startActivity(new Intent(this, FingerprintInitializationActivity.class));
        finish();
    }
}
