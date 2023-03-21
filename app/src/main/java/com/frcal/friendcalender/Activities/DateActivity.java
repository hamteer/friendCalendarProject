package com.frcal.friendcalender.Activities;

import static com.frcal.friendcalender.Activities.AddDateActivity.AddDateActivity.createRFCString;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.Exception.InputFormatException;
import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.AsyncCalEvent;
import com.google.api.client.util.DateTime;

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
import java.util.ArrayList;
import java.util.Collections;

// TODO: all
public class DateActivity extends AppCompatActivity implements EventManager.EventManagerListener, AsyncCalEvent {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    TextView chooseFriends;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn, deleteBtn;

    // Variables for DB integration
    EventManager eventManager;
    CalenderEvent currentEvent;

    // Variables for Friend selection dialogue
    boolean[] selectedFriends;
    ArrayList<String> listOfFriends = new ArrayList<>();
    ArrayList<Integer> listOfSelectedFriends = new ArrayList<>();

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
        initFriendsDialogue();
        initButtons();
    }

    private void initFriendsDialogue() {
        // first, add the always needed options to add either all or no friends:
        listOfFriends.add("privater Termin");
        listOfFriends.add("öffentlicher Termin");
        // now fill in the listOfFriends with all Friends saved in the DB (or the API):
        // TODO!
        // we use a few example friends so the code still works, but this is still todo!
        listOfFriends.add("Achim");
        listOfFriends.add("Emma");
        listOfFriends.add("Sebastian");
        // usw., with DB/API this is probably done in a for-/foreach-loop

        // CAUTION:
        // this list has to have a specific order:
        // the first two items are already declared (private and public)
        // after that, the user's friends have to be listed IN ALPHABETICAL ORDER!

        // we initialize the boolean Array that shows us which options are selected:
        selectedFriends = new boolean[listOfFriends.size()];

        // now we handle the user interaction with the TextView
        chooseFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog and set it non cancelable
                AlertDialog.Builder builder = new AlertDialog.Builder(DateActivity.this);
                builder.setTitle("Termin teilen");
                builder.setCancelable(false);

                // as setMultiChoiceItems, the method used to initialize the dropdown menu, needs an Array,
                // we need to transform our ArrayList into an Array:
                String[] arrayOfFriends = new String[listOfFriends.size()];
                for (int i = 0; i < listOfFriends.size(); i++) {
                    arrayOfFriends[i] = listOfFriends.get(i);
                }

                builder.setMultiChoiceItems(arrayOfFriends, selectedFriends, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // check condition
                        if (isChecked) {
                            listOfSelectedFriends.add(which);
                            Collections.sort(listOfSelectedFriends);
                        } else {
                            listOfSelectedFriends.remove(Integer.valueOf(which));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // test if event is made private, if yes, set text of TextView accordingly
                        if (listOfSelectedFriends.contains(0)) {
                            chooseFriends.setText(getResources().getString(R.string.private_date_set));
                            // as the event is not shared, no API call is necessary here.
                            if (listOfSelectedFriends.contains(1)) {
                                Toast.makeText(DateActivity.this, "Termin kann nicht öffentlich UND privat sein!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // event is not private, next, we need to check if event is public
                            if (listOfSelectedFriends.contains(1)) {
                                chooseFriends.setText(getResources().getString(R.string.public_date_set));
                                // TODO: API call, sync this event with all Friends this user has added to his account
                            } else {
                                // event is neither private nor public, but shared with a few specific friends
                                // TODO: API call:
                                //  either in for loop for each selected friend,
                                //  or using the existing for loop to build another list to transfer to the API
                                //  depending on what methods the API has

                                StringBuilder stringBuilder = new StringBuilder();

                                for (int j = 0; j < listOfSelectedFriends.size(); j++) {
                                    // concat array value
                                    stringBuilder.append(arrayOfFriends[listOfSelectedFriends.get(j)]);
                                    // check condition
                                    if (j != listOfSelectedFriends.size() - 1) {
                                        // When j value  not equal
                                        // to lang list size - 1
                                        // add comma
                                        stringBuilder.append(", ");
                                    }
                                }
                                // set text on textView
                                chooseFriends.setText(stringBuilder.toString());
                            }
                        }
                    }
                });

                builder.setNegativeButton("Zurück", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Alle löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedFriends.length; j++) {
                            // remove all selection
                            selectedFriends[j] = false;
                            // clear language list
                            listOfSelectedFriends.clear();
                            // clear text view value
                            chooseFriends.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void initUI() {
        chooseFriends = findViewById(R.id.edit_date_choose_friends_multiselect);

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

                    updateEvent(5, "primary", updatedEvent.eventID, title, desc, loc, from, to);

                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event, if it did not already exist beforehand
                }
                Toast.makeText(DateActivity.this, "Termin gespeichert", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventManager.deleteEvent(currentEvent);
                // TODO:
                //  - API-Call: delete this event
                deleteEvent(4,"primary",currentEvent.googleEventID);
                //  - delete the notification for this event, if it exists
                Toast.makeText(DateActivity.this, "Termin gelöscht", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onEventListUpdated() {

    }

    public void updateEvent(Integer mtdNr, String calendarID, String eventID,String summary, String description, String location, DateTime startTime, DateTime endTime /*, List<String> attendees */) {


        try {

           /* DateTime startDateTime = convertDateTime(start, date);
            DateTime endDateTime = convertDateTime(end, date); */

               /* LinkedList <String> attendees = new LinkedList<>();
                attendees.add("freundeskalender.kerim@gmail.com"); */
            CalendarEvents event5 = new CalendarEvents(mtdNr, this, calendarID,eventID ,summary, description, location, startTime, endTime /*, attendees */);
            event5.delegate=this;
            event5.setConfig();
            event5.execute();
        } catch (Exception e) {

        }
        //Datenbank Speicherung aber woher EventId vlt erstmal alles mit eventlist holen?


    }

    public void deleteEvent(Integer mtdNr,  String calendarID, String eventID) {
        CalendarEvents event4 = new CalendarEvents(4, this, "Hier KalenderID", currentEvent.googleEventID);
        event4.delegate=this;
        event4.setConfig();
        event4.execute();


    }



    @Override
    public void respGetEvent(Object res) {

    }

    @Override
    public void respInsertEvent(Object res) {

    }

    @Override
    public void respDeleteEvent(Object res) {

    }

    @Override
    public void respUpdateEvent(Object res) {

    }
}
