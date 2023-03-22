package com.frcal.friendcalender.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.Exception.InputFormatException;

import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.RestAPIClient.AsyncCalEvent;
import com.frcal.friendcalender.RestAPIClient.CalendarEvents;

import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import com.google.api.client.util.DateTime;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.TimeZone;

// TODO:
//  - API-Call
//  - Notification


public class AddDateActivity extends AppCompatActivity implements EventManager.EventManagerListener, AsyncCalEvent<String>, CalenderManager.CalenderManagerListener  {
    EditText editTitle, editDate, editTimeFrom, editTimeTo, editDesc, editLoc;
    TextView chooseFriends;
    String title, desc, loc, dateString, fromString, toString;
    DateTime from, to;
    CheckBox googleSync, notif;
    Button saveBtn;

    // variables for DB integration
    EventManager eventManager;
    CalenderManager calenderManager;

    // variables for friend selection dialogue
    boolean[] selectedFriends;
    ArrayList<String> listOfFriends = new ArrayList<>();
    ArrayList<Integer> listOfSelectedFriends = new ArrayList<>();
    ArrayList<Calender> calenderList = new ArrayList<>();

    AddDateActivity context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs",
                MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean("fingerprintSwitchState", false);
        if ((getIntent().getAction() != null && getIntent().getAction().equals(
                getString(R.string.newly_opened_action))) && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(
                    getString(R.string.intent_key), this.getClass().getCanonicalName()));
            finish();
        }
        setContentView(R.layout.activity_add_date);
        initUI();
        initFriendsDialogue();
        initStartEndTimeAutomatism();
        initButton(this);
    }

    private void initFriendsDialogue() {
        // first, add the always needed options to add either all or no friends:
        listOfFriends.add("privater Termin");
        listOfFriends.add("öffentlicher Termin");
        // now fill in the listOfFriends with all Friends saved in the DB:
        calenderManager.requestUpdate();
        for (Calender friend: calenderList) {
            listOfFriends.add(friend.name);
            Log.d("initFriendsDialogue", "added " + friend.name);
        }
        // CAUTION:
        // this list has to have a specific order:
        // the first two items are already declared (private and public)
        // after that, the user's friends have to be listed!

        // we initialize the boolean Array that shows us which options are selected:
        selectedFriends = new boolean[listOfFriends.size()];

        // now we handle the user interaction with the TextView
        chooseFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog and set it non cancelable
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDateActivity.this);
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
                                Toast.makeText(AddDateActivity.this, "Termin kann nicht öffentlich UND privat sein!", Toast.LENGTH_SHORT).show();
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
        chooseFriends = findViewById(R.id.add_date_choose_friends_multiselect);

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

        eventManager = new EventManager(getApplicationContext(),this);
        calenderManager = new CalenderManager(getApplicationContext(),this);
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
                DateTime fromWithOffset = null;
                DateTime toWithOffset = null;

                try {
                    from = DateTime.parseRfc3339(
                            getRFC3339FormattedString(dateString, fromString));
                    to = DateTime.parseRfc3339(
                            getRFC3339FormattedString(dateString, toString));

                    TimeZone timeZone = TimeZone.getDefault();
                    int fromOffset = timeZone.getOffset(from.getValue());
                    long fromDeviceTime = from.getValue() - fromOffset;
                    fromWithOffset = new DateTime(fromDeviceTime);

                    int toOffset = timeZone.getOffset(to.getValue());
                    long toDeviceTime = to.getValue() - toOffset;
                    toWithOffset = new DateTime(toDeviceTime);


                } catch (Exception e) {
                    InputFormatException ife = new InputFormatException(getApplicationContext());
                    ife.notifyUser();
                }

                if (fromString.compareToIgnoreCase(toString) > 0) {
                    Toast.makeText(AddDateActivity.this, "Bitte gültige Zeiten angeben!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                NotificationPublisher publisher = new NotificationPublisher();
                int id;
                if (notif.isChecked()) {
                    id = publisher.getUniqueNotificationId(context);
                } else {
                    id = 0;
                }

                // TODO:
                //  - DB-Call: Change calenderID, creator
                SharedPreferences sh_clid = getSharedPreferences("MainCal-ID", MODE_PRIVATE);
                String creator = sh_clid.getString("Cal-ID", "");
                CalenderEvent event = new CalenderEvent("primary", null, null, fromWithOffset, toWithOffset, desc, title, loc, creator, from, id);
                eventManager.addEvent(event);

                SharedPreferences sharedPreferences = getSharedPreferences(
                        getString(R.string.preference_name),
                        MODE_PRIVATE);
                boolean googleSignedIn = sharedPreferences.getBoolean(
                        getString(R.string.google_preference_name), false);
                if (googleSync.isChecked()) {
                    if (googleSignedIn == false) {
                        Toast.makeText(AddDateActivity.this, "Lokalen Termin angelegt (nicht eingeloggt)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // TODO:
                    //  - API-Call: use previously created CalenderEvent object to also create a event in the user's Google Calendar
                    CalenderManager cM1 = new CalenderManager(getApplicationContext(),context);
                    List<Calender> mailList = new ArrayList<>(cM1.getCalenders());
                    List<String> attendes = new ArrayList<>();


                    if(listOfSelectedFriends.contains(1))
                    {
                       for(Calender cal : mailList)
                       {
                           attendes.add(cal.calenderID);
                       }
                    }

                    addEvent(3, "primary",event.eventID ,title, desc, loc, fromWithOffset, toWithOffset ,attendes);

                }

                if (notif.isChecked()) {
                    // TODO:
                    //  - set Notification for this Event
                    publisher.scheduleNotification(context, event.eventID, title,
                            event.notificationID, from.getValue(), 15);
                }

//                startActivity(new Intent(AddDateActivity.this, SingleDayActivity.class).putExtra(
//                        "SELECTED_DATE", getCalendarDay(event.startTime.getValue())));

                Toast.makeText(AddDateActivity.this, "Termin gespeichert!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

//    private CalendarDay getCalendarDay(long milliseconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliseconds);
//        return CalendarDay.from(calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//    }

    public static String getRFC3339FormattedString(String dateString, String time) {

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = null;

            // Validate dateString
            if (dateString == null || dateString.isEmpty()) {
                throw new IllegalArgumentException("dateString cannot be null or empty");
            }

            // Validate time
            if (time == null || time.isEmpty()) {
                throw new IllegalArgumentException("time cannot be null or empty");
            } else {
                String[] timeParts = time.split(":");
                if (timeParts.length != 2) {
                    throw new IllegalArgumentException("Invalid time format: " + time);
                }
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);
                if (hours < 0 || hours > 23) {
                    throw new IllegalArgumentException("Invalid hour value: " + hours);
                }
                if (minutes < 0 || minutes > 59) {
                    throw new IllegalArgumentException("Invalid minute value: " + minutes);
                }
            }

            date = inputFormat.parse(dateString + " " + time);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            outputFormat.setTimeZone(TimeZone.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onEventListUpdated() {

    }

    private void addEvent(Integer mtdNr, String calendarID, String eventID,String summary, String description, String location, DateTime startTime, DateTime endTime , List<String> attendees ) {


        try {
            CalendarEvents event3 = new CalendarEvents(mtdNr, this, calendarID,eventID ,summary, description, location, startTime, endTime , attendees );
            event3.delegate=this;
            event3.setConfig();
            event3.execute();
        } catch (Exception e) {
            e.printStackTrace();
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


    @Override
    public void onCalenderListUpdated() {
        calenderList = calenderManager.getCalenders();
    }
    @Override
    public void respGetEvent(String res) {

    }

    @Override
    public void respInsertEvent(String res) {

    }

    @Override
    public void respDeleteEvent(String res) {

    }

    @Override
    public void respUpdateEvent(String res) {

    }
}
