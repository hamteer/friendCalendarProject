package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.frcal.friendcalender.Activities.RecyclerView.DateListRecyclerAdapter;
import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.R;
import com.google.api.client.util.DateTime;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Comparator;

public class SingleDayActivity extends AppCompatActivity implements DateListRecyclerAdapter.DateListAdapterListener, EventManager.EventManagerListener {
    TextView headline;
    RecyclerView eventList;
    EventManager eventManager;
    DateListRecyclerAdapter dateListAdapter;
    CalendarDay selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("SELECTED_DATE")) {
            initEvents();
            initUI();
            eventManager.requestUpdate();
        } else {
            startActivity(new Intent(this, CalendarActivity.class));
            finish();
        }
    }

    private void initEvents() {
        eventManager = new EventManager(getApplicationContext(), this);
    }

    private void initUI() {
        setContentView(R.layout.activity_single_day);
        headline = findViewById(R.id.single_day_headline);
        eventList = findViewById(R.id.date_list);

        // set headline to display selected day:
        Bundle extras = getIntent().getExtras();
        selectedDate = (CalendarDay) extras.get("SELECTED_DATE");
        String selectedDateString =
                selectedDate.getDay() + "." + selectedDate.getMonth() + "." + selectedDate.getYear();
        headline.setText("Termine am " + selectedDateString);

        // Activity als Listener auf Adapter registrieren:
        dateListAdapter = new DateListRecyclerAdapter(this);
        eventList.setAdapter(dateListAdapter);
    }

    // get current eventlist and setEvents in adapter
    // TODO: filter events to show only events of selected day
    @Override
    public void onEventListUpdated() {

        String day = (selectedDate.getDay() < 10) ? "0" + selectedDate.getDay() : String.valueOf(
                selectedDate.getDay());
        String month = (selectedDate.getMonth() < 10) ? "0" + selectedDate.getMonth() :
                String.valueOf(
                        selectedDate.getMonth());

        DateTime date = new DateTime(selectedDate.getYear() + "-" + month + "-" + day);
        // check if selectedDay equals startTime-Day => add event to list which gets displayed
        ArrayList<CalenderEvent> eventsOfSelectedDay = new ArrayList<>();
        for (CalenderEvent event : eventManager.getEvents()) {
            if (event.startTime.toString().substring(0, 10).equals(
                    date.toString().substring(0, 10))) {
                eventsOfSelectedDay.add(event);
            }
        }
        eventsOfSelectedDay.sort(Comparator.comparing(CalenderEvent::getStartTimeToString));

        dateListAdapter.setEvents(eventsOfSelectedDay);
        //dateListAdapter.setEvents(eventManager.getEvents());
    }

    @Override
    public void onItemSelected(String eventID) {
        Intent intent = new Intent(this, DateActivity.class);
        intent.putExtra("SELECTED_EVENT", eventID);
        startActivity(intent);
        finish();
    }

}