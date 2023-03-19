package com.frcal.friendcalender.Activities;

import static com.frcal.friendcalender.Activities.AddDateActivity.createRFCString;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;
import com.google.api.client.util.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// TODO: all
public class DateActivity extends AppCompatActivity {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn, deleteBtn;
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
        initUI();
        loadDateInfo();
        initButtons();
    }

    private void initUI() {
        editTitle = findViewById(R.id.edit_date_title);
        editDate = findViewById(R.id.edit_date_day);
        editTimeFrom = findViewById(R.id.edit_date_from);
        editTimeTo = findViewById(R.id.edit_date_to);
        editDesc = findViewById(R.id.edit_date_description);
        editLoc = findViewById(R.id.edit_date_location);

        googleSync = findViewById(R.id.edit_date_google_sync_check);
        notif = findViewById(R.id.edit_date_set_notif_check);

        saveBtn = findViewById(R.id.edit_date_save_btn);
        deleteBtn = findViewById(R.id.edit_date_delete_btn);
    }

    private void loadDateInfo() {
        // TODO:
        //  - load information of the event specified in the intent out of DB
    }

    private void initButtons() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take all the information from the editText boxes:
                title = editTitle.getText().toString();
                if (title.equals("")) title = "Mein Termin";
                desc = editDesc.getText().toString();
                loc = editLoc.getText().toString();
                dateString =  editDate.getText().toString();
                fromString = editTimeFrom.getText().toString();
                toString = editTimeTo.getText().toString();

                // create RFC3339-Strings out of start and end time and create DateTime Objects for them:
                from = DateTime.parseRfc3339(createRFCString(dateString, fromString));
                to = DateTime.parseRfc3339(createRFCString(dateString, toString));
                // TODO:
                //  - DB-Call: Update Calendar Event with information given here

                if (googleSync.isChecked()) {
                    // TODO:
                    //  - API-Call: use previously created CalenderEvent object to also update the event in the user's Google Calendar
                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event, if it did not already exist beforehand
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:
                //  - DB-Call: delete this event
                //  - API-Call: delete this event
                //  - delete the notification for this event, if it exists
            }
        });
    }
    public void updateEvent()
    {
        CheckBox checkBox = findViewById(R.id.add_date_google_sync_check);
        boolean isChecked = checkBox.isChecked();

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


        if (isChecked == true) {
            try {

                DateTime startDateTime= convertDateTime(start,date);
                DateTime endDateTime= convertDateTime(end,date);

               /* LinkedList <String> attendees = new LinkedList<>();
                attendees.add("freundeskalender.kerim@gmail.com"); */
                CalendarEvents event5 = new CalendarEvents(5, this, "andoidprojekt1@gmail.com", summary, description, location, startDateTime, endDateTime /*, attendees */);
                event5.setConfig();
                event5.execute();
            } catch (Exception e) {

            }
            //Datenbank Speicherung aber woher EventId vlt erstmal alles mit eventlist holen?


        }

    }

    public void deleteEvent()
    {



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
}
