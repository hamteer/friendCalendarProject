package com.frcal.friendcalender.RestAPIClient;


import com.google.android.gms.auth.api.identity.SignInClient;



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
