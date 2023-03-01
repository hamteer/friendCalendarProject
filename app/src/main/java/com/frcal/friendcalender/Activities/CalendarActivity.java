package com.frcal.friendcalender.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.frcal.friendcalender.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import com.frcal.friendcalender.Decorators.OneDayDecorator;

// TODO:
//  - DB-Anbindung & API-Anbindung, Aufruf bei Start, um Termine anzuzeigen
//  - Ausklappbares Menü zum Auswählen der anzuzeigenden Kalender (Burgermenü in ActionBar?)

public class CalendarActivity extends AppCompatActivity {

    FloatingActionButton addButton, addCalButton, addDateButton;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator(this);
    private MaterialCalendarView calendarView;

    private ImageView settings_action_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initCalendarView();
        initUI();
        initActionBar();
    }

    private void initUI() {
        addButton = (FloatingActionButton) findViewById(R.id.add_fab);
        addCalButton = (FloatingActionButton) findViewById(R.id.add_cal_fab);
        addDateButton = (FloatingActionButton) findViewById(R.id.add_date_fab);
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
        // TODO: DB-Aufruf für Termine:
        // new DBSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());


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

    @Override
    protected void onPause() {
        super.onPause();
        addCalButton.setVisibility(View.INVISIBLE);
        addDateButton.setVisibility(View.INVISIBLE);
        addCalButton.setEnabled(false);
        addDateButton.setEnabled(false);
    }
}