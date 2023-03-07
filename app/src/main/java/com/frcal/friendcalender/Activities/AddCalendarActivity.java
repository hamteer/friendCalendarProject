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
        Button myButton = findViewById(R.id.startGetCalendar);
        AddCalendarActivity selfRef = this;

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*THREAD
                //String calenderid = rest_client.getCalendar(access_token);
                RestAPICl threadAPI = new RestAPICl(1,access_token);
                // Beispielcode: Zeigen Sie eine Toast-Nachricht an, um den Benutzer zu benachrichtigen, dass der Button geklickt wurde
                threadAPI.start();
                try {
                    // Warte auf den Abschluss des Threads
                    threadAPI.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                RestAPICl restCl = new RestAPICl(1, selfRef);
                restCl.execute();
                /*
                String [] SCOPES = {"https://www.googleapis.com/auth/calendar.readonly"};

                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(selfRef, Arrays.asList(SCOPES))
                        .setSelectedAccountName("freundeskalender.kerim@gmail.com");
                // Calender client
                Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(application_name).build();

                // Retrieve the calendar
                try {
                    com.google.api.services.calendar.model.Calendar calendar = service.calendars().get("primary").execute();
                    Toast.makeText(getApplicationContext(), "ID: "+calendar.getId(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                Toast.makeText(getApplicationContext(), "Response Zero:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Authentication FIN",
                                Toast.LENGTH_SHORT)
                        .show();
            } else {

            }
        }
    }
}
