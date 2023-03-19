package com.frcal.friendcalender.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.frcal.friendcalender.DataAccess.CalenderManager;
import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.AsyncCalListCl;
import com.frcal.friendcalender.RestAPIClient.CalendarListCl;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AsyncCalListCl<String>, CalenderManager.CalenderManagerListener {

    private static final String TAG = "FrCal";

    private CalenderManager calenderManager;

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 50;
    private boolean showOneTapUI = true;


    // for verfiy token
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.sync_calendar).setVisibility(View.GONE);
        //For shared Preferences
        Switch fingerprintSwitch = findViewById(R.id.fingerprintSwitch);
        SharedPreferences prefs = getSharedPreferences("frcalSharedPrefs", MODE_PRIVATE);
        boolean switchState = prefs.getBoolean("fingerprintSwitchState", false);
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
        findViewById(R.id.sync_calendar).setOnClickListener(this);


        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();



    }

    //For Google-Login with Token
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        //Intent signInIntent = googleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_GET_TOKEN);
        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.server_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });

    }

    private void signOut() {
        oneTapClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }
    private void syncCalendar() {
        SharedPreferences sh_clid = getSharedPreferences("MainCal-ID", MODE_PRIVATE);
        CalendarListCl calClient = new CalendarListCl(1,this, sh_clid.getString("Cal-ID", ""));
        calClient.delegate = this;
        calClient.execute();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Verifier verObj = new Verifier(idToken);
                        verObj.execute();
                        Log.d(TAG, "Got ID token.");

                        updateUI(credential);
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d(TAG, "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d(TAG, "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d(TAG, "Couldn't get credential from result."
                                    + e.getLocalizedMessage());
                            break;
                    }
                }
                break;
        }
    }


    private void updateUI(@Nullable SignInCredential account) {
        if (account != null) {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_in);


            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.sync_calendar).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.status)).setText(R.string.signed_out);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.sync_calendar).setVisibility(View.GONE);
        }
    }

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
                signOut();
                break;
            case R.id.sync_calendar:
                syncCalendar();
                break;
        }
    }

    //Schnittstelle zu AsyncTask
    @Override
    public void respListCalList(String res) {
        JSONParser parser = new JSONParser();
        JSONArray json = null;
        calenderManager = new CalenderManager(getApplicationContext(),this);
        try {
            json = (JSONArray) parser.parse(res);
            for (Object obj : json) {
                JSONObject jsonObject = (JSONObject) obj;
                String calendarId = (String) jsonObject.get("id");
                String calendarName = (String) jsonObject.get("summary");
                calenderManager.addCalender(new Calender(calendarId,calendarName,"default"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void respInsertCalList(String res) {

    }

    @Override
    public void respGetCalList(String res) {

    }

    @Override
    public void onCalenderListUpdated() {
        ArrayList <Calender> calenderArrayList = calenderManager.getCalenders();
        Log.d("CalenderActivity", "onCalenderListUpdated() called");
        // TODO: Adapter to show Calenders which are stored in calenderArrayList
    }

    private class Verifier extends AsyncTask<Void,Void,Void> {
        private String idToken;
        Verifier (String idToken) {
            this.idToken = idToken;
        }
        public void verifyToken(String idTokenString) {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(Collections.singletonList(getString(R.string.server_client_id)))
                    // Or, if multiple clients access the backend:
                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();

            // (Receive idTokenString by HTTPS POST)

            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(idTokenString);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");
                SharedPreferences sharedPreferences = getSharedPreferences("MainCal-ID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Cal-ID", email);
                editor.apply();
                // Use or store profile information
                // ...
            } else {
                System.out.println("Invalid ID token.");
            }
    }
        @Override
        protected Void doInBackground(Void... voids) {
            verifyToken(idToken);
            return null;
        }
    }
}