package com.frcal.friendcalender.Activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;

import com.frcal.friendcalender.Authentication.AuthenticationManager;
import com.frcal.friendcalender.R;

public class FingerprintInitializationActivity extends AppCompatActivity {

    // If the fingerprint lock is already set up, this activity will be skipped
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
                MODE_PRIVATE);
        boolean fingerprintEnabled = sharedPreferences.getBoolean(
                getString(R.string.fingerprint_preference_name), false);
        if (fingerprintEnabled) {
            endActivity(sharedPreferences, true);
        } else {
            initUI(sharedPreferences, savedInstanceState);
        }
    }

    // Initialize UI
    private void initUI(SharedPreferences sharedPreferences, Bundle bundle) {
        setContentView(R.layout.activity_fingerprint_initialization);

        Button agreeButton = findViewById(R.id.agree_button_fingerprint_initialization);
        Button disagreeButton = findViewById(R.id.disagree_button_fingerprint_initialization);

        agreeButton.setOnClickListener((View v) ->
                setAuthenticationMethod(sharedPreferences, bundle));

        disagreeButton.setOnClickListener((View v) -> endActivity(sharedPreferences, false));
    }

    // Set authentication method via AuthenticationManager and close activity
    private void setAuthenticationMethod(SharedPreferences sharedPreferences, Bundle bundle) {
        BiometricManager biometricManager = BiometricManager.from(this);
        AuthenticationManager.setAuthenticationMethod(this, bundle);
        endActivity(sharedPreferences, biometricManager.canAuthenticate(
                BIOMETRIC_STRONG | DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS);
    }

    // Depending on the button state, the preferences for the fingerprint are set and the next activity is started
    // finish() is called so the user cannot accidentally return to this activity
    private void endActivity(SharedPreferences sharedPreferences, boolean fingerprintState) {
        sharedPreferences.edit().putBoolean(
                getString(R.string.fingerprint_preference_name),
                fingerprintState).apply();
        startActivity(new Intent(this, GoogleInitializationActivity.class));
        finish();
    }
}
