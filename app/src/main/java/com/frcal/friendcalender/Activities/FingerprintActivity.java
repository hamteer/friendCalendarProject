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
import android.widget.Toast;

import com.frcal.friendcalender.R;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    enum authenticationMethod {BIOMETRIC_PROMPT, DEVICE_CREDENTIAL_PROMPT}

    private static final int REQUEST_CODE = 99999;

    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticationMethod biometricFeature = checkForBiometricFeature();
        initBiometricPrompt();
        if (biometricFeature == authenticationMethod.BIOMETRIC_PROMPT) {
            initUI(biometricFeature);
            showBiometricPrompt();
        } else {
            initUI(biometricFeature);
            showDeviceCredentialPrompt();
        }

    }

    private void initUI(authenticationMethod biometricFeature) {
        setContentView(R.layout.activity_fingerprint);

        ImageView viewFingerprint = findViewById(R.id.view_fingerprint);
        Button loginButton = findViewById(R.id.authentication_button);

        if (biometricFeature == authenticationMethod.BIOMETRIC_PROMPT) {
            loginButton.setVisibility(View.GONE);
            loginButton.setEnabled(false);
            viewFingerprint.setVisibility(View.VISIBLE);
            viewFingerprint.setEnabled(true);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setEnabled(true);
            viewFingerprint.setVisibility(View.GONE);
            viewFingerprint.setEnabled(false);
        }

        viewFingerprint.setOnClickListener(view -> showBiometricPrompt());
        loginButton.setOnClickListener(view -> showDeviceCredentialPrompt());

    }

    private authenticationMethod checkForBiometricFeature() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("frCal", "App can authenticate using biometrics.");
                return authenticationMethod.BIOMETRIC_PROMPT;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d("frCal", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d("frCal", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Log.d("frCal", "Biometric security update is required.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Log.d("frCal",
                        "Biometric features are not supported by the current android version.");
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Log.d("frCal", "Biometric features are unknown. Error may occur");
                return authenticationMethod.BIOMETRIC_PROMPT;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                final Intent enrollIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                } else {
                    enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                }
                startActivity(enrollIntent);
                return authenticationMethod.BIOMETRIC_PROMPT;
        }
        if (biometricManager.canAuthenticate(
                DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            final Intent enrollIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        DEVICE_CREDENTIAL);
            } else {
                enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            }
            startActivity(enrollIntent);
        }
        return authenticationMethod.DEVICE_CREDENTIAL_PROMPT;
    }

    private void initBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String activity = getIntent().getStringExtra(getString(R.string.intent_key));

                        try {
                            startActivity(new Intent(FingerprintActivity.this,
                                    Class.forName(activity)));
                            Toast.makeText(getApplicationContext(), "Authentication succeeded!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (ClassNotFoundException e) {
                            Toast.makeText(getApplicationContext(), activity + " Authentication succeeded, but there was an error opening the activity",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FingerprintActivity.this,
                                    CalendarActivity.class));
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder().setTitle(
                        getString(R.string.biometric_prompt_title)).setSubtitle(
                        getString(R.string.biometric_prompt_subtitle)).setAllowedAuthenticators(
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL).build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showDeviceCredentialPrompt() {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder().setTitle(
                        getString(R.string.device_credential_prompt_title)).setSubtitle(
                        getString(
                                R.string.device_credential_prompt_subtitle)).setAllowedAuthenticators(
                        DEVICE_CREDENTIAL).build();

        biometricPrompt.authenticate(promptInfo);
    }

}