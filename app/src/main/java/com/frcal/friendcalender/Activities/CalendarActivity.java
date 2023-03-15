package com.frcal.friendcalender.Activities;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

        SharedPreferences sharedPreferences = getSharedPreferences("frcalSharedPrefs",
                MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean("fingerprintSwitchState", false);
//        boolean firstRunOfApp = sharedPreferences.getBoolean("firstRun", true);
//        if (firstRunOfApp) {
//            startActivity(new Intent(this, NotificationInitializationActivity.class));
//        } else
        if ((getIntent().getAction() != null && (getIntent().getAction().equals(
                "android.intent.action.MAIN") || getIntent().getAction().equals(
                "android.intent.action.VIEW_LOCUS"))) && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(
                    getString(R.string.intent_key), this.getClass().getCanonicalName()));
            finish();
        }
        setContentView(R.layout.activity_calendar);
        initCalendarView();
        initUI();
        initActionBar();

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
        // TODO: DB-Aufruf für Termine:
        // new DBSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());


        // OnClickListener für Tage
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                // create Intent, put date in extras, start SingleDateActivity
                Log.d("FrCal",
                        "in: onDateSelected, selected Day: " + date.getDay() + "." + date.getMonth() + "." + date.getYear());
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

}