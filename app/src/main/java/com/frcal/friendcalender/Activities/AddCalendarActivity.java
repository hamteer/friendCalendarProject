package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.R;


public class AddCalendarActivity extends AppCompatActivity implements CalenderManager.CalenderManagerListener {

    CalenderManager calenderManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationPublisher publisher = new NotificationPublisher();
        publisher.createNotificationChannel(this);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean fingerprintActive = sharedPreferences.getBoolean(
                getString(R.string.fingerprint_preference_name), false);
        if ((getIntent().getAction() != null && getIntent().getAction().equals(
                getString(R.string.newly_opened_action))) && fingerprintActive) {
            startActivity(new Intent(this, FingerprintActivity.class).putExtra(
                    getString(R.string.intent_key), this.getClass().getCanonicalName()));
            finish();
        }
        setContentView(R.layout.activity_add_calendar);
        initUI();
    }

    private void initUI() {
        Button addCalendarButton = findViewById(R.id.add_calendar_save_btn);
        EditText addCalendarMail = findViewById(R.id.add_calendar_mail);
        calenderManager = new CalenderManager(getApplicationContext(),this);
        addCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = addCalendarMail.getText().toString();
                // test if EditText has valid input:
                if (mail.equals("")) {
                    Toast.makeText(AddCalendarActivity.this, "Bitte Mailadresse angeben!", Toast.LENGTH_SHORT).show();
                } else if (!testMailUsingRegex(mail)) {
                    Toast.makeText(AddCalendarActivity.this, "Bitte gültige Mailadresse angeben!", Toast.LENGTH_SHORT).show();
                } else {
                    Calender calender = new Calender(mail,mail,null);
                    calenderManager.addCalender(calender);
                    //Log.d("CalenderActivity", "Added Calender to database");
                    addCalendarMail.setText("");
                    Toast.makeText(AddCalendarActivity.this, "Neuer Kalender wurde hinzugefügt!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    private static boolean testMailUsingRegex(String mail) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return mail.matches(regexPattern);
    }

    @Override
    public void onCalenderListUpdated() {

    }
}
