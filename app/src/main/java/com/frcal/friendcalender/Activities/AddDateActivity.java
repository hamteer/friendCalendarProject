package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.google.api.client.util.DateTime;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;

// TODO:
//  - UI
//  - Funktionalität


public class AddDateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs",
                MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean("fingerprintSwitchState", false);
        if (getIntent().getAction() != null && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(
                    getString(R.string.intent_key), this.getClass().getCanonicalName()));
            finish();
        }
        setContentView(R.layout.activity_add_date);

    }

    private void addEvent()
    {
        //Woher bekomme ich die Kalender ID bei AddDateActivity?
        //ID ? Bei der Übergabe in die Datenbank benötigt man eine ID, Welche aber von Google automatisch bestimmt wird
        //package DatabaseEntities; wird rot markiert ist es richtig?
        //Woher attendees
        CheckBox checkBox = findViewById(R.id.add_date_google_sync_check);
        boolean isChecked = checkBox.isChecked();

        EditText editText = findViewById(R.id.add_date_title);
        String summary = editText.getText().toString();

        editText = findViewById(R.id.add_date_day);
        String date = editText.getText().toString();

        editText = findViewById(R.id.add_date_from);
        String start = editText.getText().toString();

        editText = findViewById(R.id.add_date_to);
        String end = editText.getText().toString();

        editText = findViewById(R.id.add_date_description);
        String description = editText.getText().toString();

        editText = findViewById(R.id.add_date_location);
        String location = editText.getText().toString();


        if (isChecked == true) {
            try {

                DateTime startDateTime= convertDateTime(start,date);
                DateTime endDateTime= convertDateTime(end,date);

               /* LinkedList <String> attendees = new LinkedList<>();
                attendees.add("freundeskalender.kerim@gmail.com"); */
                CalendarEvents event3 = new CalendarEvents(3, this, "andoidprojekt1@gmail.com", summary, description, location, startDateTime, endDateTime /*, attendees */);
                event3.setConfig();
                event3.execute();
            } catch (Exception e) {

            }
            //Datenbank Speicherung aber woher EventId vlt erstmal alles mit eventlist holen?


        }

    }
    public DateTime convertDateTime(String time, String date)
    {
        String DateTimeString = date + "" + time;
        LocalDateTime DateTime = LocalDateTime.parse(DateTimeString);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = DateTime.atZone(zoneId);
        // Konvertieren Sie das ZonedDateTime-Objekt in ein Instant-Objekt
        Instant instant = zonedDateTime.toInstant();

        // Konvertieren Sie das Instant-Objekt in ein DateTime-Objekt mit der Default-Zeitzone
        DateTime dateTime = new DateTime(instant.toEpochMilli());

        return dateTime;

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
            Log.w("", "handleSignInResult:error", e);

        }
        //Datenbankaufruf
    }


}
