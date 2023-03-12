package com.frcal.friendcalender.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;
import com.frcal.friendcalender.RestAPIClient.CalendarListCl;
import com.frcal.friendcalender.RestAPIClient.RestAPICl;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.oauth2.AccessToken;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

// TODO:
//  - UI
//  - Funktionalität
public class AddCalendarActivity extends AppCompatActivity {
    private static final String TAG = "IdTokenActivity";

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
        Button myButton2 = findViewById(R.id.debug2);
        Button myButton3 = findViewById(R.id.debug3);
        AddCalendarActivity selfRef = this;

        SharedPreferences sharedPreferences = getSharedPreferences("MainCal-ID", Context.MODE_PRIVATE);
        // Holen Sie sich die Calendar-ID
        String calendarID = sharedPreferences.getString("Cal-ID", "");
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*RestAPICl restCl = new RestAPICl(1, selfRef);
                restCl.execute();*/
                CalendarListCl calListCl = new CalendarListCl(1, selfRef, calendarID);
                calListCl.execute();
                Toast.makeText(getApplicationContext(), "Response Zero:", Toast.LENGTH_SHORT).show();
            }
        });
        myButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*RestAPICl restCl = new RestAPICl(1, selfRef);
                restCl.execute();*/
                CalendarListCl calListCl = new CalendarListCl(2, selfRef, calendarID);
                calListCl.execute();
                Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_SHORT).show();
            }
        });
        myButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*RestAPICl restCl = new RestAPICl(1, selfRef);
                restCl.execute();*/
              /*  CalendarListCl calListCl = new CalendarListCl(3, selfRef, calendarID);
                calListCl.execute();
                Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_SHORT).show(); */
            }
        });
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
    public void readEventData(JSONObject eventData) {
        try {
            String id = eventData.getString("id");
            String description = eventData.getString("description");
            JSONObject endObject = eventData.getJSONObject("end");
            String endDateTime = endObject.getString("dateTime");
            JSONObject startObject = eventData.getJSONObject("start");
            String startDateTime = startObject.getString("dateTime");
            String summary = eventData.getString("summary");
            String location = eventData.getString("location");
        }catch(Exception e)
        {
            Log.w(TAG, "handleSignInResult:error", e);

        }
    }
    private void testFunction()
    {
        CalendarEvents event = new CalendarEvents(1,this,"andoidprojekt1@gmail.com","03n3mvu6k4a73084tkh5hdkgeg"); //Get event
        event.setConfig();
        event.execute();
/*
        CalendarEvents event2 = new CalendarEvents(2,this,"andoidprojekt1@gmail.com"); //List
        event2.setConfig();
        event2.execute();

        List<String> l1 = new LinkedList<String>();
        l1.add("hildnersilke@gmail.com");
        DateTime start = new DateTime("2023-03-13T09:00:00-07:00");
        DateTime end = new DateTime("2023-03-14T09:00:00-07:00");
        CalendarEvents event3 = new CalendarEvents(3,this,"andoidprojekt1@gmail.com","1234","sumary","description","Coburg",start,end,l1);
        event3.setConfig();
        event3.execute();
//delete
        CalendarEvents event4 = new CalendarEvents(1,this,"andoidprojekt1@gmail.com","03n3mvu6k4a73084tkh5hdkgeg");
        event4.setConfig();
        event4.execute();
*/


    }

}
