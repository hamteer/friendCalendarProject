package com.frcal.friendcalender.Activities;

import static com.frcal.friendcalender.Activities.AddDateActivity.createRFCString;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.R;
import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.frcal.friendcalender.RestAPIClient.CalendarEventList;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;
import com.google.api.client.util.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// TODO: all
public class DateActivity extends AppCompatActivity implements EventManager.EventManagerListener {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn, deleteBtn;

    EventManager eventManager;
    CalenderEvent currentEvent;
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

        eventManager = new EventManager(getApplicationContext(),this);
    }

    /**
     * get event by getting eventID from extra "SELECTED_EVENT" and then calling eventManager method
     */
    private void loadDateInfo() {
        // TODO:
        //  - load information of the event specified in the intent out of DB
        currentEvent = eventManager.getEventByEventID(getIntent().getStringExtra("SELECTED_EVENT"));
        editTitle.setText(currentEvent.summary);
        // transform to dd.mm.yyyy
        String startTimeString = currentEvent.startTime.toString();
        String displayDate = startTimeString.substring(8,10) + "." + startTimeString.substring(5,7) + "." + startTimeString.substring(0,4);
        editDate.setText(displayDate);
        // transform to hh:mm
        String displayStartTime = startTimeString.substring(11,13) + ":" + startTimeString.substring(14,16);
        String endTimeString = currentEvent.endTime.toString();
        String displayEndTime = endTimeString.substring(11,13) + ":" + endTimeString.substring(14,16);;
        editTimeFrom.setText(displayStartTime);
        editTimeTo.setText(displayEndTime);
        editDesc.setText(currentEvent.description);
        editLoc.setText(currentEvent.location);
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
                try {
                    from = DateTime.parseRfc3339(createRFCString(dateString, fromString, getApplicationContext()));
                    to = DateTime.parseRfc3339(createRFCString(dateString, toString, getApplicationContext()));
                } catch (Exception e) {
                    InputFormatException ife = new InputFormatException(getApplicationContext());
                    ife.notifyUser();
                }


                if (fromString.compareToIgnoreCase(toString) > 0) {
                    Toast.makeText(DateActivity.this, "Bitte gültige Zeiten angeben!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CalenderEvent updatedEvent = new CalenderEvent(currentEvent.calenderID, currentEvent.eventID, currentEvent.googleEventID,from,to,desc,title,loc, currentEvent.creator,new DateTime(System.currentTimeMillis()));
                eventManager.updateEvent(updatedEvent);
                if (googleSync.isChecked()) {
                    // TODO:
                    //  - API-Call: use previously created CalenderEvent object to also update the event in the user's Google Calendar
                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event, if it did not already exist beforehand
                }
                Toast.makeText(DateActivity.this,"Termin gespeichert", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventManager.deleteEvent(currentEvent);
                // TODO:
                //  - API-Call: delete this event
                //  - delete the notification for this event, if it exists
                Toast.makeText(DateActivity.this, "Termin gelöscht", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onEventListUpdated() {

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

    public void evaluateJsonEventList()
    {

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