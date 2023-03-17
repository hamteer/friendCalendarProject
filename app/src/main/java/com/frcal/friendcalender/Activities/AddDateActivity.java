package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.R;
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
        initButton();
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

        saveBtn = findViewById(R.id.add_date_save_btn);

        eventManager = new EventManager(getApplicationContext(),this);
    }

    private void initButton() {
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
                //  - DB-Call: Change calenderID, creator, googleEventID,updated

                CalenderEvent event = new CalenderEvent(null,null, null,from,to,desc,title,loc,null,from);
                eventManager.addEvent(event);
                if (googleSync.isChecked()) {
                    // TODO:
                    //  - API-Call: use previously created CalenderEvent object to also create a event in the user's Google Calendar
                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event
                }
            }
        });
    }

    static String createRFCString(String dateString, String timeString) {
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
        String rfcString = dayMonthYear[2] + "-" + dayMonthYear[1] + "-" + dayMonthYear[0] + "T" + hrMin[0] + ":" + hrMin[1] + ":00" ;
        return rfcString;
    }

    @Override
    public void onEventListUpdated() {

    }
}
