package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.frcal.friendcalender.Activities.RecyclerView.DateListRecyclerAdapter;
import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class SingleDayActivity extends AppCompatActivity implements DateListRecyclerAdapter.DateListAdapterListener, EventManager.EventManagerListener {
    TextView headline;
    RecyclerView eventList;
    EventManager eventManager;
    DateListRecyclerAdapter dateListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvents();
        initUI();
        eventManager.requestUpdate();
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
        CalendarDay selectedDate = (CalendarDay) extras.get("SELECTED_DATE");
        String selectedDateString = selectedDate.getDay() + "." + selectedDate.getMonth() + "." + selectedDate.getYear();
        headline.setText("Termine am " + selectedDateString);

        // Activity als Listener auf Adapter registrieren:
        dateListAdapter = new DateListRecyclerAdapter(this);
        eventList.setAdapter(dateListAdapter);
    }

    @Override
    public void onItemSelected(CalenderEvent event) {
        Intent intent = new Intent(this, DateActivity.class);
        intent.putExtra("SELECTED_EVENT", event.eventID);
        startActivity(intent);
    }

    // get current eventlist and setEvents in adapter
    // TODO: filter events to show only events of selected day
    @Override
    public void onEventListUpdated() {
        dateListAdapter.setEvents(eventManager.getEvents());
    }
}