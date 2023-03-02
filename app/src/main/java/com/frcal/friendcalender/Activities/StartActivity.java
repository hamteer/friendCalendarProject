package com.frcal.friendcalender.Activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.frcal.friendcalender.R;

import java.util.concurrent.Executor;

// TODO:
//  - Dokumentation
//  - Deaktivierung des Sensors je nach Nutzereinstellung
public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99999;
    private ImageView viewFingerprint;

    private Button viewStartButton;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        viewFingerprint = findViewById(R.id.viewFingerprint);
        viewStartButton = findViewById(R.id.startButton);
        //load shared preferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("switchState", false);
        if (switchState) {
            viewFingerprint.setVisibility(View.VISIBLE);
            viewStartButton.setVisibility(View.INVISIBLE);
        } else {
            viewFingerprint.setVisibility(View.INVISIBLE);
            viewStartButton.setVisibility(View.VISIBLE);
        }
        // for fingerprint-login
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("frCal", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d("frCal", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d("frCal", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(StartActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(StartActivity.this, CalendarActivity.class));
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        viewFingerprint.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });

        viewStartButton.setOnClickListener(view -> {
            startActivity(new Intent(StartActivity.this, CalendarActivity.class));
            Toast.makeText(getApplicationContext(),
                    "No authentication", Toast.LENGTH_SHORT).show();
        });

    }
}