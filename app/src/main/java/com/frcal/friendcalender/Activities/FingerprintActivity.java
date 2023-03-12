package com.frcal.friendcalender.Activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frcal.friendcalender.R;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    private enum authenticationMethod {
        BIOMETRIC_AUTHENTICATION,
        DEVICE_CREDENTIAL_AUTHENTICATION,
        NO_AUTHENTICATION
    }

    private BiometricPrompt prompt;

    private final String tag = "FingerprintActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Überprüfung, ob biometrische Feature auf Gerät vorhanden und notfalls dessen
        // Initialisierung
        // 2. Initialisierung der Methoden des BiometricPrompt, die nach Eingabe der
        // Authentifizierungsmethode ausgeführt werden
        // 3. Initialisierung der UI anhand der möglichen Authentifizierungsmethode
        // 4. Anzeige des Authentifizierungsdialogs ohne zusätzliche Nutzereingabe
        authenticationMethod authMethod = checkForBiometricFeature();
        initPrompt();
        initUI(authMethod);
        showPrompt(authMethod);
    }

    // Je nach Authentifizierungsmethode wird ein Bild eines Fingerabdrucks und eine Unterschrift
    // oder ein Button angezeigt
    // Außerdem wird der Untertitel entsprechend der angesteuerten Activity angepasst
    private void initUI(authenticationMethod authMethod) {
        setContentView(R.layout.activity_fingerprint);

        TextView subtitle = findViewById(R.id.subtitle_fingerprint_activity);
        // Anpassen des Untertitels
        try {
            String activity = Class.forName(getIntent().getStringExtra(
                    getString(R.string.intent_key))).getSimpleName();
            subtitle.setText(
                    String.format(getString(R.string.subtitle_fingerprint_activity), activity));
        } catch (ClassNotFoundException e) {
            subtitle.setText(
                    String.format(getString(R.string.subtitle_fingerprint_activity), "App"));
            throw new RuntimeException(e);
        }

        ImageView fingerprintImage = findViewById(R.id.fingerprint_view);
        TextView fingerprintAction = findViewById(R.id.description_fingerprint_image);
        Button loginButton = findViewById(R.id.authentication_button);

        // Anzeigen der entsprechenden UI
        if (authMethod == authenticationMethod.BIOMETRIC_AUTHENTICATION) {
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
                view -> showPrompt(authenticationMethod.BIOMETRIC_AUTHENTICATION));
        loginButton.setOnClickListener(
                view -> showPrompt(authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION));

    }

    // Es wird überprüft, ob die Authentifizierung über Fingerabdruck möglich und bereits
    // eingerichtet ist
    // Andernfalls muss eine andere Authentifizierungsmethode (PIN, Muster oder Passwort)
    // verwendet werden
    private authenticationMethod checkForBiometricFeature() {
        BiometricManager biometricManager = BiometricManager.from(this);

        // Überprüfung der möglichen Authentifizierung über Fingerabdruck
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(tag, "App can authenticate using biometrics.");
                return authenticationMethod.BIOMETRIC_AUTHENTICATION;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d(tag, "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d(tag, "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Log.d(tag, "Biometric security update is required.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Log.d(tag,
                        "Biometric features are not supported by the current android version.");
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Log.d(tag, "Biometric features are unknown. Error may occur");
                return authenticationMethod.BIOMETRIC_AUTHENTICATION;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                enrollAuthenticationMethod(authenticationMethod.BIOMETRIC_AUTHENTICATION);
                return authenticationMethod.BIOMETRIC_AUTHENTICATION;
        }

        // Überprüfung, ob reguläre Authentifizierungsmethode eingerichtet wurde
        if (biometricManager.canAuthenticate(
                DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            enrollAuthenticationMethod(authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION);
        }
        return authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION;
    }

    // Falls die notwendige Authentifizierungsmethode noch nicht eingerichtet ist, wird der
    // Nutzer in die Systemeinstellungen geleitet um dies nachzuholen
    private void enrollAuthenticationMethod(authenticationMethod authMethod) {
        Intent enrollIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
            if (authMethod == authenticationMethod.BIOMETRIC_AUTHENTICATION) {
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
            } else {
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        DEVICE_CREDENTIAL);
            }
        } else if (authMethod == authenticationMethod.BIOMETRIC_AUTHENTICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
        } else {
            enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        }
        startActivity(enrollIntent);
    }

    // Da der Nutzer die Systemeinstellungen verlassen kann ohne eine Authentifizierugsmethode
    // hinzugefügt haben, sollte vor Anzeigen des Authentifizierungsdialogs immer wieder
    // überprüft werden, ob eine Authentifizierungsmethode existiert
    private authenticationMethod checkForAuthenticationMethod() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(
                BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            return authenticationMethod.BIOMETRIC_AUTHENTICATION;
        } else if (biometricManager.canAuthenticate(
                DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
            return authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION;
        } else {
            return authenticationMethod.NO_AUTHENTICATION;
        }
    }

    // Definition der Reaktion auf verschiedene Ergebnisse nach Eingabe der
    // Authentifizierungsmethode
    // Bei Fehlern wird dem Nutzer ein Toast angezeigt, während bei erfolgreicher
    // Authentifizierung zur Zielactivity gesprungen wird, sofern diese ausgelesen werden kann
    // Andernfalls wird in die CalendarActivity gesprungen und der Nutzer wird über ein Toast
    // darüber informiert
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
                        String activity = getIntent().getStringExtra(
                                getString(R.string.intent_key));
                        try {
                            startActivity(new Intent(FingerprintActivity.this,
                                    Class.forName(activity)));
                            finish();
                        } catch (ClassNotFoundException e) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.fingerprint_redirection),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FingerprintActivity.this,
                                    CalendarActivity.class));
                        }
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

    // Je nach Authentifizierungsmethode wird die Beschreibung des Dialogs angepasst
    // Wenn trotzvorheriger Bestimmung der Authentifizierungsmethode kein Fingerabdruck mehr
    // vorhanden ist, wird die Alternative der regulären Authentifizierungsmethode genutzt und
    // die UI im Nachhinein angepasst
    // Falls der Nutzer keine Authentifizierungsmethode eingerichtet hat, wird ihm dies über ein
    // Toast angezeigt
    private void showPrompt(authenticationMethod authMethod) {
        authenticationMethod authPrompt = checkForAuthenticationMethod();
        if (authMethod != authPrompt) {
            initUI(authPrompt);
        }
        if (authPrompt == authenticationMethod.NO_AUTHENTICATION) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.fingerprint_no_authentification_method),
                    Toast.LENGTH_SHORT).show();
        } else {
            String subtitle = getString(R.string.device_credential_prompt_subtitle);
            int allowedAuthenticators = DEVICE_CREDENTIAL;
            if (authPrompt == authenticationMethod.BIOMETRIC_AUTHENTICATION) {
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
    }
}

