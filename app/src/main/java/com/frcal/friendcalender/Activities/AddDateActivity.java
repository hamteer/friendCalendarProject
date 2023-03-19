package com.frcal.friendcalender.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.R;

// TODO:
//  - UI
//  - Funktionalität


public class AddDateActivity extends AppCompatActivity {

    private boolean notificationsCheckboxEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean(
                getString(R.string.fingerprint_preference_name), false);
        if ((getIntent().getAction() != null && getIntent().getAction().equals(
                "android.intent.action.VIEW_LOCUS")) && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(
                    getString(R.string.intent_key), this.getClass().getCanonicalName()));
            finish();
        }
        setContentView(R.layout.activity_add_date);
    }
}
