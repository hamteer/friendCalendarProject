package com.frcal.friendcalender.Activities;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.Decorators.EventDecorator;
import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.AsyncCalLEventList;
import com.frcal.friendcalender.RestAPIClient.CalendarEventList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.util.DateTime;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import com.frcal.friendcalender.Decorators.OneDayDecorator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO:
//  - API-Anbindung

public class CalendarActivity extends AppCompatActivity implements EventManager.EventManagerListener, CalenderManager.CalenderManagerListener, AsyncCalLEventList {

    FloatingActionButton addButton, addCalButton, addDateButton;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator(this);
    private MaterialCalendarView calendarView;

    private CalenderManager calenderManager;
    private EventManager eventManager;


    private ImageView settings_action_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);
        initCalendarView();
        initUI();
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventList();

    }

    private void initUI() {
        addButton = (FloatingActionButton) findViewById(R.id.add_fab);
        addCalButton = (FloatingActionButton) findViewById(R.id.add_cal_fab);
        addDateButton = (FloatingActionButton) findViewById(R.id.add_date_fab);

        addCalButton.setVisibility(View.INVISIBLE);
        addDateButton.setVisibility(View.INVISIBLE);
        addCalButton.setEnabled(false);
        addDateButton.setEnabled(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCalButton.setVisibility(View.VISIBLE);
                addDateButton.setVisibility(View.VISIBLE);
                addCalButton.setEnabled(true);
                addDateButton.setEnabled(true);
            }
        });
        addCalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCalendarActivity.class);
                startActivity(intent);
            }
        });
        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initCalendarView() {
        // initialer Setup des CalendarView
        AndroidThreeTen.init(this);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
        // set calendar arrows to white if darkmode is enabled:
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                calendarView.setLeftArrow(R.drawable.arrow_left_darkmode);
                calendarView.setRightArrow(R.drawable.arrow_right_darkmode);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                calendarView.setLeftArrow(R.drawable.arrow_left_lightmode);
                calendarView.setRightArrow(R.drawable.arrow_right_lightmode);
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        // Modus zwischen Monats- und Wochenansicht wechseln
        Button btn = (Button) findViewById(R.id.mode_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarView.getCalendarMode() == CalendarMode.WEEKS) {
                    calendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
                } else {
                    calendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
                }
            }
        });

        // aktuellen Tag farbig hervorheben:
        calendarView.addDecorator(oneDayDecorator);
        // bei App-Start ist inital auch der aktuelle Tag ausgewählt:
        calendarView.setSelectedDate(CalendarDay.today());


        // Grafische Aufbereitung von Tagen, an denen Termine vorhanden sind
        calenderManager = new CalenderManager(getApplicationContext(), this);
        eventManager = new EventManager(getApplicationContext(), this);


        // OnClickListener für Tage
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // create Intent, put date in extras, start SingleDateActivity
                Log.d("FrCal", "in: onDateSelected, selected Day: " + date.getDay() + "." + date.getMonth() + "." + date.getYear());
                Intent intent = new Intent(getApplicationContext(), SingleDayActivity.class);
                intent.putExtra("SELECTED_DATE", date);
                startActivity(intent);
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar);
            settings_action_bar = actionBar.getCustomView().findViewById(R.id.settings_action_bar);

            settings_action_bar.setOnClickListener(view -> {
                startActivity(new Intent(CalendarActivity.this, SettingsActivity.class));
            });
        }
    }

    // gets called when CalenderList gets updated
    @Override
    public void onCalenderListUpdated() {
        ArrayList<Calender> calenderArrayList = calenderManager.getCalenders();
        Log.d("CalenderActivity", "onCalenderListUpdated() called");
    }

    // gets called when EventList gets updated
    @Override
    public void onEventListUpdated() {
        ArrayList<CalenderEvent> eventArrayList = eventManager.getEvents();
        Log.d("CalenderActivity", "onEventListUpdated() called");
        ArrayList<CalendarDay> daysToDecorate = new ArrayList<>();
        for (CalenderEvent event : eventArrayList) {
            Log.d("CalenderActivity", event.startTime.toString());
            String timeString = event.startTime.toString();
            String year = timeString.substring(0, 4);
            String month = timeString.substring(5, 7);
            String day = timeString.substring(8, 10);
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            CalendarDay calendarDay = CalendarDay.from(date);
            daysToDecorate.add(calendarDay);
        }
        calendarView.removeDecorators();
        calendarView.addDecorator(new EventDecorator(Color.RED, daysToDecorate));
    }

    public void evaluateJsonEventList(List<String> jsonList) {

        List<String> eventIDList = new ArrayList<>();
        List<String> summaryList = new ArrayList<>();
        List<String> locationList = new ArrayList<>();
        List<DateTime> startTimeList = new ArrayList<>();
        List<DateTime> endTimeList = new ArrayList<>();
        //   String calenderID;
        try {
            for (String jsonString : jsonList) {
                JSONObject json = new JSONObject(jsonString);
                // calenderID = json.getString("id");
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject event = items.getJSONObject(i);
                    eventIDList.add(event.getString("id"));
                    summaryList.add(event.getString("summary"));
                    locationList.add(event.getString("location"));

                    JSONObject startObj = event.getJSONObject("start");
                    String startDateTime = startObj.getString("dateTime");
                    String[] parts = startDateTime.split("T");
                    String startDate = parts[0];
                    String startTime = parts[1].substring(0, 8);
                    startTimeList.add(convertDateTime(startTime, startDate));

                    JSONObject endObj = event.getJSONObject("ends");
                    String endDateTime = endObj.getString("dateTime");
                    String endDate = parts[0];
                    String endTime = parts[1].substring(0, 8);
                    endTimeList.add(convertDateTime(endTime, endDate));


                }
            }
        } catch (Exception e) {

        }

    }

    public void getEventList() {
        CalendarEventList event2 = new CalendarEventList(2, this, "primary");
        event2.delegate = this;
        event2.setConfig();
        event2.execute();


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


    @Override
    public void respGetEventList(List res) {
        List<String> eventIDList = new ArrayList<>();
        List<String> summaryList = new ArrayList<>();
        List<String> locationList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        List<DateTime> startTimeList = new ArrayList<>();
        List<DateTime> endTimeList = new ArrayList<>();
        EventManager eventManager1 = new EventManager(getApplicationContext(), this);
        ArrayList<CalenderEvent> liste = new ArrayList<>(eventManager1.getEvents());


        //   String calenderID;
        try {
            for (Object jsonString : res) {
                JSONObject json = new JSONObject(jsonString.toString());
                // calenderID = json.getString("id");
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    boolean compare = false;
                    JSONObject event = items.getJSONObject(i);
                    eventIDList.add(event.getString("id"));
                    summaryList.add(event.getString("summary"));
                    locationList.add(event.getString("location"));
                    descriptionList.add(event.getString("description"));

                    JSONObject startObj = event.getJSONObject("start");
                    String startDateTime = startObj.getString("dateTime");
                    String[] parts = startDateTime.split("T");
                    String startDate = parts[0];
                    String startTime = parts[1].substring(0, 8);
                    startTimeList.add(convertDateTime(startTime, startDate));

                    JSONObject endObj = event.getJSONObject("ends");
                    String endDateTime = endObj.getString("dateTime");
                    String endDate = parts[0];
                    String endTime = parts[1].substring(0, 8);
                    endTimeList.add(convertDateTime(endTime, endDate));
                    //CalenederActivity wo bekomme ich die event id aus der Datenbank her
                    for (CalenderEvent eventDB : liste) {
                        if (eventDB.googleEventID.equals(event.getString("id"))) {
                            compare = true;
                        }
                    }
                    if (compare == false) {
                        CalenderEvent eventDB = new CalenderEvent("primary", null, event.getString("id"), convertDateTime(startTime, startDate), convertDateTime(endTime, endDate), event.getString("description"), event.getString("summary"), event.getString("location"), null, null);
                        eventManager.addEvent(eventDB);
                    }


                }
            }
            eventManager.requestUpdate();
        } catch (Exception e) {

        }

    }
}