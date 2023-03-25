package com.frcal.friendcalender.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.Decorators.EventDecorator;
import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.AsyncCalLEventList;
import com.frcal.friendcalender.RestAPIClient.CalendarEventList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import com.frcal.friendcalender.Decorators.OneDayDecorator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

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

        NotificationPublisher publisher = new NotificationPublisher();
        publisher.createNotificationChannel(this);

        setContentView(R.layout.activity_calendar);
        initCalendarView();
        initUI();
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventManager.requestUpdate();
        calenderManager.requestUpdate();
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
        calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
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
                    calendarView.state().edit().setCalendarDisplayMode(
                            CalendarMode.MONTHS).commit();
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
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                // create Intent, put date in extras, start SingleDateActivity
                ////Log.d("FrCal", "in: onDateSelected, selected Day: " + date.getDay() + "." + date.getMonth() + "." + date.getYear());
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

    @Override
    protected void onPause() {
        super.onPause();
        getEventList();
    }

    // gets called when CalenderList gets updated
    @Override
    public void onCalenderListUpdated() {
        ArrayList<Calender> calenderArrayList = calenderManager.getCalenders();
        //Log.d("CalenderActivity", "onCalenderListUpdated() called");
    }

    // gets called when EventList gets updated
    @Override
    public void onEventListUpdated() {
        ArrayList<CalenderEvent> eventArrayList = eventManager.getEvents();
        //Log.d("CalenderActivity", "onEventListUpdated() called");
        ArrayList<CalendarDay> daysToDecorate = new ArrayList<>();
        for (CalenderEvent event : eventArrayList) {
            //Log.d("CalenderActivity", event.startTime.toString());
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

                    JSONObject endObj = event.getJSONObject("ends");
                    String endDateTime = endObj.getString("dateTime");

                }
            }
        } catch (Exception e) {

        }

    }

    public void getEventList() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean googleSignedIn = sharedPreferences.getBoolean(
                getString(R.string.google_preference_name), false);
        if (googleSignedIn ==true) {
            CalendarEventList event2 = new CalendarEventList(2, this, "primary");
            event2.delegate = this;
            event2.setConfig();
            event2.execute();
        }
    }

    public DateTime convertDateTime(String date) {
        return DateTime.parseRfc3339(date);

    }


    @Override
    public void respGetEventList(List res) {
        EventManager eventManager1 = new EventManager(getApplicationContext(), this);
        ArrayList<CalenderEvent> liste = new ArrayList<>(eventManager1.getEvents());
        List<Event> result = res;
        boolean compare=false;
        //   String calenderID;
        try {
            for (Event ev : result) {
                compare=false;
                    for (CalenderEvent eventDB : liste) {

                        if (ev.getId().equals(eventDB.googleEventID)) {
                            compare = true;
                            break;
                        }
                    }
                    if (compare == false) {
                        DateTime datdeb = ev.getEnd().getDateTime();
                        // if EndTime is not set => skip
                        if (datdeb == null){
                            continue;
                        }
                        CalenderEvent eventDB = new CalenderEvent("primary", null, ev.getId(), ev.getStart().getDateTime(), ev.getEnd().getDateTime(), ev.getDescription(), ev.getSummary(), ev.getLocation(), null, null, 1 );
                        eventManager.addEvent(eventDB);
                    }
            }
            eventManager.requestUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
