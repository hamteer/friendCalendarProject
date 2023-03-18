package com.frcal.friendcalender.Activities;

import static com.frcal.friendcalender.Activities.AddDateActivity.createRFCString;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.metrics.Event;
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

// TODO: all
public class DateActivity extends AppCompatActivity implements EventManager.EventManagerListener {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn, deleteBtn;

    EventManager eventManager;
    CalenderEvent date;
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

        eventManager = new EventManager(getApplicationContext(),this);
    }

    /**
     * get event by getting eventID from extra "SELECTED_EVENT" and then calling eventManager method
     */
    private void loadDateInfo() {
        // TODO:
        //  - load information of the event specified in the intent out of DB
        date = eventManager.getEventByEventID(getIntent().getStringExtra("SELECTED_EVENT"));
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

                // TODO:
                //  - DB-Call: Update Calendar Event with information given here
                //  - Timestamp for updated needed

                CalenderEvent updatedEvent = new CalenderEvent(date.calenderID,date.eventID,date.googleEventID,from,to,desc,title,loc,date.creator,null);
                eventManager.updateEvent(updatedEvent);
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

    @Override
    public void onEventListUpdated() {

    }
}
