package com.frcal.friendcalender.RestAPIClient;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.frcal.friendcalender.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;

import java.io.Serializable;

public class SharedOneTabClient {
    private static SharedOneTabClient instance = null;
    private SignInClient oneTapClient = null;

    public static SharedOneTabClient getInstance() {
        if (instance == null) {
            instance = new SharedOneTabClient();
        }
        return instance;
    }

    public SignInClient getSignInClient() {
        if (oneTapClient!=null) {
            return oneTapClient;
        }
        return null;
    }
    public void setSignInClient(SignInClient oneTapClient) {
        if (this.oneTapClient==null)
            this.oneTapClient=oneTapClient;
    }
}
