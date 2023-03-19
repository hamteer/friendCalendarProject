package com.frcal.friendcalender.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;

public class GoogleInitializationActivity extends AppCompatActivity {

    // Falls der Nutzer bereits bei Google angemeldet ist, wird die Activity übersprungen
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean googleSignedIn = sharedPreferences.getBoolean(
                getString(R.string.google_preference_name), false);
        if (googleSignedIn) {
            endActivity(sharedPreferences, true);
        } else {
            initUI(sharedPreferences);
        }

    }

    // TODO: Google Anbindung hier
    // UI wird initialisiert
    private void initUI(SharedPreferences sharedPreferences) {
        setContentView(R.layout.activity_google_initialization);

        Button agreeButton = findViewById(R.id.agree_button_google_initialization);
        Button disagreeButton = findViewById(R.id.disagree_button_google_initialization);

        agreeButton.setOnClickListener((View v) ->
                Toast.makeText(this, "TODO: Google Anbindung hier", Toast.LENGTH_SHORT).show());

        disagreeButton.setOnClickListener((View v) -> endActivity(sharedPreferences, false));
    }

    // Je nach Knopfdruck wird die Einstellung für den Google Login gesetzt und die nächste
    // Activity wird aufgerufen
    // Damit nicht zu dieser Activity zurückgekehrt werden kann, wird finish() aufgerufen
    private void endActivity(SharedPreferences sharedPreferences, boolean loginState) {
        sharedPreferences.edit().putBoolean(
                getString(R.string.google_preference_name),
                loginState).apply();
        sharedPreferences.edit().putBoolean(getString(R.string.first_run_preference_name), false).apply();
        startActivity(new Intent(this, CalendarActivity.class));
        finish();
    }
}
