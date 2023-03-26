package com.frcal.friendcalender.RestAPIClient;


import com.google.android.gms.auth.api.identity.SignInClient;


/* You can only Sign-out if you have the same SignInClient-Object. Therfore we need the Singleton "SharedOneTabClient".
   So SharedOneTabClient.getInstance() creates this object only one time. You can access this object and its instance variable oneTabClient
   from multiple Activitys and do a sign in or out. 
*/

public class SharedOneTabClient {
    private static SharedOneTabClient instance = null;
    private SignInClient oneTapClient = null;
    
    //Create instatnce only one time
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
