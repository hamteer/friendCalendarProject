package com.frcal.friendcalender.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;

import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
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

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// TODO:
//  - DB-Call
//  - API-Call
//  - Notification


public class AddDateActivity extends AppCompatActivity implements EventManager.EventManagerListener {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn;

    EventManager eventManager;

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
        initUI();
        initStartEndTimeAutomatism();
        initButton(this);
    }

    private void initStartEndTimeAutomatism() {
        editTimeFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        String input = editTimeFrom.getText().toString();
                        String inputHrs = input.substring(0, 2);
                        int inputHrsInt = Integer.parseInt(inputHrs);
                        int outputHrsInt = inputHrsInt + 1;
                        String outputHrs = String.valueOf(outputHrsInt);
                        if (outputHrs.length() == 1) {
                            String outputText = "0" + outputHrs + ":00";
                            editTimeTo.setText(outputText);
                        } else {
                            String outputText = outputHrs + ":00";
                            editTimeTo.setText(outputText);
                        }
                    } catch (Exception e) {
                        InputFormatException ife = new InputFormatException(
                                getApplicationContext());
                        ife.notifyUser();
                    }
                }
            }
        });
    }

    private void initUI() {
        editTitle = findViewById(R.id.add_date_title);
        editDate = findViewById(R.id.add_date_day);
        editDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
        editTimeFrom = findViewById(R.id.add_date_from);
        editTimeTo = findViewById(R.id.add_date_to);
        editDesc = findViewById(R.id.add_date_description);
        editLoc = findViewById(R.id.add_date_location);

        googleSync = findViewById(R.id.add_date_google_sync_check);
        notif = findViewById(R.id.add_date_set_notif_check);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name), MODE_PRIVATE);
        boolean notificationsActive = sharedPreferences.getBoolean(
                getString(R.string.notifications_preference_name), false);
        notif.setEnabled(notificationsActive);
        notif.setChecked(notificationsActive);

        saveBtn = findViewById(R.id.add_date_save_btn);

        eventManager = new EventManager(getApplicationContext(), this);
    }

    private void initButton(Context context) {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take all the information from the editText boxes:
                title = editTitle.getText().toString();
                if (title.equals("")) title = "Mein Termin";
                desc = editDesc.getText().toString();
                loc = editLoc.getText().toString();
                dateString = editDate.getText().toString();
                fromString = editTimeFrom.getText().toString();
                toString = editTimeTo.getText().toString();

                // create RFC3339-Strings out of start and end time and create DateTime Objects
                // for them:
                try {
                    from = DateTime.parseRfc3339(
                            createRFCString(dateString, fromString, getApplicationContext()));
                    to = DateTime.parseRfc3339(
                            createRFCString(dateString, toString, getApplicationContext()));
                } catch (Exception e) {
                    InputFormatException ife = new InputFormatException(getApplicationContext());
                    ife.notifyUser();
                }

                if (fromString.compareToIgnoreCase(toString) > 0) {
                    Toast.makeText(AddDateActivity.this, "Bitte gültige Zeiten angeben!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                // TODO:
                //  - DB-Call: Change calenderID, creator, googleEventID,updated

                CalenderEvent event = new CalenderEvent(null, null, null, from, to, desc, title,
                        loc, null, from);
                eventManager.addEvent(event);
                if (googleSync.isChecked()) {
                    // TODO:
                    //  - API-Call: use previously created CalenderEvent object to also create a
                    //  event in the user's Google Calendar
                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event
                    NotificationPublisher publisher = new NotificationPublisher();
                    publisher.scheduleNotification(context, event.eventID, title,
                            event.notificationID, from.getValue(), 15);
                }


                Toast.makeText(AddDateActivity.this, "Termin gespeichert!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    static String createRFCString(String dateString, String timeString, Context context) {
        String rfcString = "";
        try {
            String[] dayMonthYear = new String[3];
            dayMonthYear = dateString.split("\\.");
            String[] hrMin = new String[2];
            hrMin = timeString.split(":");
            TimeZone tz = TimeZone.getDefault();
            String offset = String.valueOf(tz.getRawOffset());
            String rfcOffset;
            if (offset.contains("-")) {
                if (offset.length() == 2) {
                    rfcOffset = "-0" + offset.charAt(1) + ":00";
                } else {
                    rfcOffset = offset + ":00";
                }
            } else {
                if (offset.length() == 1) {
                    rfcOffset = "+0" + offset + ":00";
                } else {
                    rfcOffset = "+" + offset + ":00";
                }
            }
            rfcString =
                    dayMonthYear[2] + "-" + dayMonthYear[1] + "-" + dayMonthYear[0] + "T" + hrMin[0] + ":" + hrMin[1] + ":00";
        } catch (Exception e) {
            InputFormatException ife = new InputFormatException(context);
            ife.notifyUser();
        }
        return rfcString;
    }

    @Override
    public void onEventListUpdated() {

    }

    private void addEvent() {
        //Woher bekomme ich die Kalender ID bei AddDateActivity?
        //ID ? Bei der Übergabe in die Datenbank benötigt man eine ID, Welche aber von Google
        // automatisch bestimmt wird
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

                DateTime startDateTime = convertDateTime(start, date);
                DateTime endDateTime = convertDateTime(end, date);

               /* LinkedList <String> attendees = new LinkedList<>();
                attendees.add("freundeskalender.kerim@gmail.com"); */
                CalendarEvents event3 = new CalendarEvents(3, this, "andoidprojekt1@gmail.com",
                        summary, description, location, startDateTime,
                        endDateTime /*, attendees */);
                event3.setConfig();
                event3.execute();
            } catch (Exception e) {

            }
            //Datenbank Speicherung aber woher EventId vlt erstmal alles mit eventlist holen?


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
        } catch (Exception e) {
            Log.w("", "handleSignInResult:error", e);

        }
        //Datenbankaufruf
    }


}