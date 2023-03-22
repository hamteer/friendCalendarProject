package com.frcal.friendcalender.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.frcal.friendcalender.R;

public class NotificationInitializationActivity extends AppCompatActivity {

    private AlertDialog alertDialog;

    private ActivityResultLauncher<String> firstPermissionRequest;
    private ActivityResultLauncher<String> secondPermissionRequest;

    // Wenn Benachrichtigungen bereits gestattet wurden, wird diese Activity übersprungen
    // Andernfalls werden Berechtigungsanfragen, AlertDialog und UI initialisiert
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean notificationsAllowed = sharedPreferences.getBoolean(
                getString(R.string.notifications_preference_name), false);
        if (notificationsAllowed && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)) {
            endActivity(sharedPreferences, true);
        } else {
            initPermissionRequests(sharedPreferences);
            initAlertDialog(sharedPreferences);
            initUI(sharedPreferences);
        }
    }

    // UI wird initialisiert
    // Je nachdem, welcher Button gedrückt wird, wird ein ActivityResultLauncher gestartet oder
    // ein AlertDialog angezeigt
    private void initUI(SharedPreferences sharedPreferences) {
        setContentView(R.layout.activity_notification_initialization);
        Button agreeButton = findViewById(R.id.agree_button_notifications_initialization);
        Button disagreeButton = findViewById(R.id.disagree_button_notifications_initialization);

        agreeButton.setOnClickListener((View v) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                firstPermissionRequest.launch(
                        Manifest.permission.POST_NOTIFICATIONS);
            } else {
                endActivity(sharedPreferences, true);
            }
        });

        disagreeButton.setOnClickListener((View v) -> showAlertDialog());
    }

    // AlertDialog wird initialisiert, wodurch der Nutzer eine zweite Chance erhält
    // Benachrichtigungen zu erlauben
    private void initAlertDialog(SharedPreferences sharedPreferences) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.notifications_initialization_alert_dialog))
                .setPositiveButton(
                        getString(R.string.notifications_initialization_alert_dialog_negative),
                        (dialog, id) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                secondPermissionRequest.launch(
                                        Manifest.permission.POST_NOTIFICATIONS);
                            } else {
                                endActivity(sharedPreferences, true);
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.notifications_initialization_alert_dialog_positive),
                        (dialog, id) -> endActivity(sharedPreferences, false)
                );

        alertDialog = builder.create();
    }

    // AlertDialog wird angezeigt
    private void showAlertDialog() {
        alertDialog.show();
    }

    // Berechtigungsanfragen werden initialisiert
    // Anfrage 1 wird erstellt, wenn auf den Agree-Button der Activity gedrückt wird
    // Anfrage 2 wird erstellt, wenn auf den positiven Button des AlertDialogs gedrückt wird
    private void initPermissionRequests(SharedPreferences sharedPreferences) {
        firstPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                endActivity(sharedPreferences, true);
                            } else {
                                showAlertDialog();
                            }
                        });
        secondPermissionRequest =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> endActivity(sharedPreferences, isGranted));
    }

    // Je nach Knopfdruck wird die Einstellung für die Benachrichtigungen gesetzt und die nächste
    // Activity wird aufgerufen
    // Damit nicht zu dieser Activity zurückgekehrt werden kann, wird finish() aufgerufen
    private void endActivity(SharedPreferences sharedPreferences, boolean notificationState) {
        sharedPreferences.edit().putBoolean(
                getString(R.string.notifications_preference_name),
                notificationState).apply();
        startActivity(new Intent(this, FingerprintInitializationActivity.class));
        finish();
    }
}
