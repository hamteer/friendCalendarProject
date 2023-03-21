package com.frcal.friendcalender.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.R;
import com.frcal.friendcalender.RestAPIClient.SharedOneTabClient;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;

public class GoogleInitializationActivity extends AppCompatActivity {

    private static final String TAG = "FrCal";
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 50;

    private boolean showOneTapUI = true;

    // for verfiy token
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_name),
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

        agreeButton.setOnClickListener((View v) -> getIdToken(sharedPreferences));

        disagreeButton.setOnClickListener((View v) -> endActivity(sharedPreferences, false));
    }

    // Je nach Knopfdruck wird die Einstellung für den Google Login gesetzt und die nächste
    // Activity wird aufgerufen
    // Damit nicht zu dieser Activity zurückgekehrt werden kann, wird finish() aufgerufen
    private void endActivity(SharedPreferences sharedPreferences, boolean loginState) {
        sharedPreferences.edit().putBoolean(
                getString(R.string.google_preference_name),
                loginState).apply();
        sharedPreferences.edit().putBoolean(getString(R.string.first_run_preference_name),
                false).apply();
        startActivity(new Intent(this, CalendarActivity.class));
        finish();
    }

    private void getIdToken(SharedPreferences sharedPreferences) {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        //Intent signInIntent = googleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_GET_TOKEN);
        SharedOneTabClient shOneTab = SharedOneTabClient.getInstance();
        shOneTab.setSignInClient(Identity.getSignInClient(this));
        oneTapClient = shOneTab.getSignInClient();
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
                            SharedPreferences sharedPreferences = getSharedPreferences(
                                    getString(R.string.preference_name),
                                    MODE_PRIVATE);
                            sharedPreferences.edit().putBoolean(getString(R.string.google_preference_name), true).apply();
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
                        GoogleInitializationActivity.Verifier verObj = new GoogleInitializationActivity.Verifier(idToken);
                        verObj.execute();
                        //LOGIN SUCCESSFUL
                        startActivity(new Intent(this, CalendarActivity.class));
                        Log.d(TAG, "Got ID token.");
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