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

        // 1. To get notifications, a standard channel will be created if it doesn't exist already
        // 2. If this is the first time the app is opened after installation, a special workflow is opened
        // 3. If the FingerprintActivity is launched as the main activity, the activation of the
        //    fingerprint in the settings needs to be checked. If it isn't, the originally wanted activity
        //    (e.g. the CalendarActivity on normal startup) is launched
        // 4. The authentication method to be used is determined
        // 5. The methods of the BiometricPrompt class, run after the authentication method is chosen,
        //    are initialized
        // 6. The AlertDialog, shown if no authentication methods exist, is initialized
        // 7. The UI is initialized according to the possible authentication methods
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

    // Depending on the authentication method, either a fingerprint icon with an explanation or a button is shown.
    // Also, the caption is modified depending on the activity that the user tries to open.
    private void initUI(AuthenticationManager.authenticationMethod authMethod) {
        setContentView(R.layout.activity_fingerprint);

        TextView subtitle = findViewById(R.id.subtitle_fingerprint_activity);

        // Modify caption
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

        // show UI according to authentication method
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

    // Reactions on different events after input of authentication method are defined
    // If there is an error or a mistake, a Toast is shown to the user
    private void initPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        prompt = new BiometricPrompt(FingerprintActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        //Log.d(tag, "Authentication error: " + errString);
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
                        //Log.d(tag, "Authentication failed");
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.fingerprint_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // The caption of the authentication dialogue is modified depending on the authentication method
    // If the fingerprint method is chosen, but there is no fingerprint saved on the device,
    // the alternative authentication method is used and the UI is changed accordingly
    // If the user has not set an authentication method for their device, they are notified via
    // an AlertDialog and can now do so
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

    // Initialisation of AlertDialog
    // The user can set up an authentication method
    // (or decide against doing so, but will then be unable to open the app for now)
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

    // Show the AlertDialog to the user to allow them to set up an authentication method
    private void showAlertDialog() {
        alertDialog.show();
    }


    // Either start the CalendarActivity if the app is opened via the icon
    // or start another target activity if any can be determined
    // If no target activity can be determined, the CalendarActivity will be started and the user
    // informed via a Toast
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

