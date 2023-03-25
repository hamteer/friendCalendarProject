package com.frcal.friendcalender.Activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.frcal.friendcalender.Authentication.AuthenticationManager;
import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.frcal.friendcalender.R;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private BiometricPrompt prompt;

    private AlertDialog alertDialog;

    private boolean alertDialogCanceled;

    private final String tag = "FingerprintActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Um Benachrichtigungen empfangen zu können, wird der Standard-Channel eingerichtet,
        // sofern er nicht bereits existiert
        // 2. Falls die App erstmalig nach ihrer Installation geöffnet wird, wird ein gesonderter
        // Workflow geöffnet
        // 3. Falls Fingerprint als Main Activity gelauncht wurde, muss überprüft werden, ob die
        // Eingabe des Fingerabdrucks überhaupt ist, sonst wird sofort die CalendarActivity, bzw.
        // die Ursprungsactivity aufgerufen
        // 4. Ermitteln der zu nutzenden Authentifizierungsmethode
        // 5. Initialisierung der Methoden des BiometricPrompt, die nach Eingabe der
        // Authentifizierungsmethode ausgeführt werden
        // 6. Initialisierung des AlertDialogs, der angezeigt wird, sofern keine
        // Authentifizierungsmethode existiert
        // 7. Initialisierung der UI anhand der möglichen Authentifizierungsmethode
        NotificationPublisher publisher = new NotificationPublisher();
        publisher.createNotificationChannel(this);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean firstRunOfApp = sharedPreferences.getBoolean(
                getString(R.string.first_run_preference_name), true);
        boolean fingerprintActive = sharedPreferences.getBoolean(
                getString(R.string.fingerprint_preference_name), false);
        if (firstRunOfApp) {
            startActivity(new Intent(this, NotificationInitializationActivity.class));
            finish();
        } else if (!fingerprintActive) {
            startNextActivity();
            finish();
        } else {
            AuthenticationManager.authenticationMethod authMethod =
                    AuthenticationManager.checkForAuthenticationMethod(this);
            initPrompt();
            initAlertDialog(savedInstanceState);
            initUI(authMethod);
        }
    }

    // Je nach Authentifizierungsmethode wird ein Bild eines Fingerabdrucks und eine Unterschrift
    // oder ein Button angezeigt
    // Außerdem wird der Untertitel entsprechend der angesteuerten Activity angepasst
    private void initUI(AuthenticationManager.authenticationMethod authMethod) {
        setContentView(R.layout.activity_fingerprint);

        TextView subtitle = findViewById(R.id.subtitle_fingerprint_activity);

        // Anpassen des Untertitels
        if (getIntent().getStringExtra(
                getString(R.string.intent_key)) != null) {
            try {
                String activity = Class.forName(getIntent().getStringExtra(
                        getString(R.string.intent_key))).getSimpleName();
                subtitle.setText(
                        String.format(getString(R.string.subtitle_fingerprint_activity), activity));
            } catch (ClassNotFoundException e) {
                subtitle.setText(
                        String.format(getString(R.string.subtitle_fingerprint_activity),
                                getString(R.string.subtitle_fingerprint_activity_class_not_found)));
                throw new RuntimeException(e);
            }
        } else {
            subtitle.setText(
                    String.format(getString(R.string.subtitle_fingerprint_activity),
                            getString(R.string.subtitle_fingerprint_activity_no_intent_extra)));
        }

        ImageView fingerprintImage = findViewById(R.id.fingerprint_view);
        TextView fingerprintAction = findViewById(R.id.description_fingerprint_image);
        Button loginButton = findViewById(R.id.authentication_button);

        // Anzeigen der entsprechenden UI
        if (authMethod == AuthenticationManager.authenticationMethod.BIOMETRIC_AUTHENTICATION) {
            fingerprintImage.setVisibility(View.VISIBLE);
            fingerprintImage.setEnabled(true);
            fingerprintAction.setVisibility(View.VISIBLE);
            fingerprintAction.setEnabled(true);
            loginButton.setVisibility(View.GONE);
            loginButton.setEnabled(false);
        } else {
            fingerprintImage.setVisibility(View.GONE);
            fingerprintImage.setEnabled(false);
            fingerprintAction.setVisibility(View.GONE);
            fingerprintAction.setEnabled(false);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setEnabled(true);
        }

        fingerprintImage.setOnClickListener(
                view -> showPrompt(
                        AuthenticationManager.authenticationMethod.BIOMETRIC_AUTHENTICATION
                ));
        loginButton.setOnClickListener(
                view -> showPrompt(
                        AuthenticationManager.authenticationMethod
                                .DEVICE_CREDENTIAL_AUTHENTICATION
                ));


    }

    // Definition der Reaktion auf verschiedene Ergebnisse nach Eingabe der
    // Authentifizierungsmethode
    // Bei Fehlern wird dem Nutzer ein Toast angezeigt
    private void initPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        prompt = new BiometricPrompt(FingerprintActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Log.d(tag, "Authentication error: " + errString);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.fingerprint_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startNextActivity();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Log.d(tag, "Authentication failed");
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.fingerprint_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Je nach Authentifizierungsmethode wird die Beschreibung des Authentifizierungs-Dialogs
    // angepasst
    // Wenn trotz vorheriger Bestimmung der Authentifizierungsmethode kein Fingerabdruck mehr
    // vorhanden ist, wird die Alternative der regulären Authentifizierungsmethode genutzt und
    // die UI im Nachhinein angepasst
    // Falls der Nutzer keine Authentifizierungsmethode eingerichtet hat, wird ihm dies über
    // einen AlertDialog vermittelt und er hat eine letzte Chance, die Einrichtung nachzuholen
    private void showPrompt(AuthenticationManager.authenticationMethod authMethod) {
        if (authMethod == AuthenticationManager.authenticationMethod.NO_AUTHENTICATION) {
            showAlertDialog();
            if (alertDialogCanceled) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.fingerprint_no_authentification_method_toast),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String subtitle = getString(R.string.device_credential_prompt_subtitle);
        int allowedAuthenticators = DEVICE_CREDENTIAL;
        if (authMethod == AuthenticationManager.authenticationMethod.BIOMETRIC_AUTHENTICATION) {
            subtitle = getString(R.string.biometric_prompt_subtitle);
            allowedAuthenticators = BIOMETRIC_STRONG | DEVICE_CREDENTIAL;
        }
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder().setTitle(
                        getString(R.string.prompt_title)).setSubtitle(
                        subtitle).setAllowedAuthenticators(
                        allowedAuthenticators).build();

        prompt.authenticate(promptInfo);
    }

    // Initialisierung des AlertDialogs
    // Der Nutzer kann eine Authentifizierungsmethode einrichten oder abbrechen, kann dann jedoch
    // nicht die App öffnen
    private void initAlertDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.fingerprint_no_authentification_method_alert_dialog))
                .setPositiveButton(
                        getString(
                                R.string.fingerprint_no_authentification_method_alert_dialog_positive),
                        (dialog, id) -> alertDialogCanceled =
                                (AuthenticationManager.setAuthenticationMethod(this,
                                        bundle) == AuthenticationManager.authenticationMethod.NO_AUTHENTICATION))
                .setNegativeButton(
                        getString(
                                R.string.fingerprint_no_authentification_method_alert_dialog_negative),
                        (dialog, id) -> alertDialogCanceled =
                                true);

        alertDialog = builder.create();
    }

    // Gibt dem Nutzer eine letzte Chance, eine Authentifizierungsmethode einzurichten
    private void showAlertDialog() {
        alertDialog.show();
    }


    // Startet entweder die CalendarActivity neu, wenn die App über das Icon gestartet wurde oder
    // die Zielactivity sofern diese ausgelesen werden kann
    // Andernfalls wird erneut in die CalendarActivity gesprungen und der Nutzer wird über ein Toast
    // darüber informiert
    private void startNextActivity() {
        if (getIntent().getStringExtra(getString(R.string.intent_key)) != null) {
            String activity = getIntent().getStringExtra(
                    getString(R.string.intent_key));
            try {
                Intent intent = new Intent(FingerprintActivity.this, Class.forName(activity));
                if (getIntent().hasExtra(getString(R.string.extra_event_key))) {
                    intent.putExtra(getString(R.string.extra_event_key),
                            getIntent().getStringExtra(getString(R.string.extra_event_key)));
                }
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.fingerprint_redirection),
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FingerprintActivity.this,
                        CalendarActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(FingerprintActivity.this,
                    CalendarActivity.class));
            finish();
        }
    }
}

