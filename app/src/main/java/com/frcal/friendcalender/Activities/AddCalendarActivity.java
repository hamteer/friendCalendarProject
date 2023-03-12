package com.frcal.friendcalender.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.CalendarCl;
import com.frcal.friendcalender.RestAPIClient.CalendarListCl;
import com.frcal.friendcalender.RestAPIClient.RestAPICl;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.oauth2.AccessToken;

import java.io.IOException;
import java.util.Arrays;

// TODO:
//  - UI
//  - Funktionalität
public class AddCalendarActivity extends AppCompatActivity {

    private String access_token;
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();;
    private static final String application_name = "My Calendar App";
    private static final int REQUEST_AUTHORIZATION = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);
        AddCalendarActivity selfRef = this;

        SharedPreferences sharedClID = getSharedPreferences("MainCal-ID", Context.MODE_PRIVATE);
        // Holen Sie sich die Calendar-ID
        String calendarID = sharedClID.getString("Cal-ID", "");

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs", MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean("fingerprintSwitchState", false);
        if (getIntent().getAction() != null && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(getString(R.string.intent_key), this.getClass().getCanonicalName()));
        } else {
            setContentView(R.layout.activity_add_calendar);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                data.getStringExtra("CalendarList");
                System.out.println();
            } else {

            }
        }
    }
}
