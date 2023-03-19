package com.frcal.friendcalender.Activities;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.frcal.friendcalender.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// TODO:
//  - wenn fingerprintSwitch aktiviert wird, gleich Fingerabdruck-Sensor aktivieren

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FrCal";
    private static final int RC_GET_TOKEN = 9002;

    private GoogleSignInClient googleSignInClient;
    private TextView idTokenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //For shared Preferences
        Switch notificationsSwitch = findViewById(R.id.notifications_switch);
        Switch fingerprintSwitch = findViewById(R.id.fingerprintSwitch);

        SharedPreferences prefs = getSharedPreferences("frcalSharedPrefs", MODE_PRIVATE);
        boolean notificationsActive = prefs.getBoolean(
                getString(R.string.notifications_preference_name), false);
        boolean switchState = prefs.getBoolean("fingerprintSwitchState", false);

        setNotificationsState(notificationsActive);
        notificationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        if (isChecked) {
                            ActivityResultLauncher<String> permissionRequest =
                                    registerForActivityResult(
                                            new ActivityResultContracts.RequestPermission(),
                                            this::setNotificationsState);
                            permissionRequest.launch(
                                    Manifest.permission.POST_NOTIFICATIONS);
                        } else {
                            setNotificationsState(false);
                        }
                    } else {
                        setNotificationsState(isChecked);
                    }
                });

        fingerprintSwitch.setChecked(switchState);
        fingerprintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getSharedPreferences("frcalSharedPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("fingerprintSwitchState", isChecked);
                editor.apply();
            }
        });

        // Button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);


        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // configure_signin
        // Request the user's ID token to identify the user to the backend.
        // This contains the user's basic profile (name, profile picture URL, etc)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(
                        "764959564302-kk5n95aabkm0sj9eae9n28l1neit61i9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void setNotificationsState(boolean allowed) {
        Switch notificationsSwitch = findViewById(R.id.notifications_switch);
        ImageView notificationImage = findViewById(R.id.notifications_switch_image);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_name),
                MODE_PRIVATE);

        if (allowed) {
            notificationImage.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.baseline_notifications_active_48));
        } else {
            notificationImage.setImageDrawable(
                    AppCompatResources.getDrawable(this, R.drawable.baseline_notifications_off_48));
        }

        notificationsSwitch.setChecked(allowed);
        prefs.edit().putBoolean(getString(
                        R.string.notifications_preference_name),
                allowed).apply();
    }

    //For Google-Login with Token
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount.
        // If the GoogleSignInAccount already has a valid token this method may complete
        // immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    private void revokeAccess() {
        googleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_in);

            String idToken = account.getIdToken();

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_out);
            idTokenTextView.setText(getString(R.string.id_token_fmt, "null"));
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                getIdToken();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

}