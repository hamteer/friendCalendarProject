package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.CalendarEventList;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO: all
public class DateActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_date);

    }

    public void updateEvent() {
        //Woher bekomme ich die Kalender ID bei AddDateActivity?
        //ID ? Bei der Übergabe in die Datenbank benötigt man eine ID, Welche aber von Google automatisch bestimmt wird
        //package DatabaseEntities; wird rot markiert ist es richtig?
        //Woher attendees
        EditText editText = findViewById(R.id.edit_date_title);
        String summary = editText.getText().toString();

        editText = findViewById(R.id.edit_date_day);
        String date = editText.getText().toString();

        editText = findViewById(R.id.edit_date_from);
        String start = editText.getText().toString();

        editText = findViewById(R.id.edit_date_to);
        String end = editText.getText().toString();

        editText = findViewById(R.id.edit_date_description);
        String description = editText.getText().toString();

        editText = findViewById(R.id.edit_date_location);
        String location = editText.getText().toString();


        try {

            DateTime startDateTime = convertDateTime(start, date);
            DateTime endDateTime = convertDateTime(end, date);

               /* LinkedList <String> attendees = new LinkedList<>();
                attendees.add("freundeskalender.kerim@gmail.com"); */
            CalendarEvents event5 = new CalendarEvents(5, this, "andoidprojekt1@gmail.com", summary, description, location, startDateTime, endDateTime /*, attendees */);
            event5.setConfig();
            event5.execute();
        } catch (Exception e) {

        }
        //Datenbank Speicherung aber woher EventId vlt erstmal alles mit eventlist holen?


    }

    public void deleteEvent() {
        CalendarEvents event4 = new CalendarEvents(4, this, "Hier KalenderID", "Hier Event ID");
        event4.setConfig();
        event4.execute();


    }

    public void getEventList()
    {
        CalendarEventList event2 = new CalendarEventList(2, this, "Hier KalenderID");
        event2.setConfig();
        event2.execute();


    }

    public void evaluateJsonEventList(List<String> jsonList)
    {

        List<String> eventIDList = new ArrayList<>();
        List<String> summaryList = new ArrayList<>();
        List<String> locationList = new ArrayList<>();
        List<EventDateTime> startTimeList = new ArrayList<>();
        List<EventDateTime> endTimeList = new ArrayList<>();
     //   String calenderID;
        try {
            for (String jsonString : jsonList) {
                JSONObject json = new JSONObject(jsonString);
               // calenderID = json.getString("id");
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject event = items.getJSONObject(i);
                    eventIDList.add(event.getString("id"));
                    summaryList.add( event.getString("summary"));
                    locationList.add( event.getString("location"));



                }
            }
        }catch(Exception e)
        {

        }

    }

    public DateTime convertDateTime(String time, String date) {
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
}
