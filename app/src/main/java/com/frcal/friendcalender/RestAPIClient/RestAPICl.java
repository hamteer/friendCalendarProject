package com.frcal.friendcalender.RestAPIClient;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.calendar.Calendar;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class RestAPICl extends AsyncTask<Void, Void, String> {

   private static final HttpTransport httpTransport = new NetHttpTransport();
   private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();;
   private static final String application_name = "My Calendar App";
   private String tokenStr;

   private Context context;

   // Funktion auswählen 1=getCalender
   private int mtdNr;
   private static final int REQUEST_AUTHORIZATION = 1;

   public RestAPICl(int mtdNr, Context context){
      this.context=context;
      this.mtdNr=mtdNr;
   };

   @Override
   protected String doInBackground(Void... voids) {
      switch (mtdNr) {
         case 1:
            return getCalendar(context);
      }
      return "ZERO";
   }

   public String getCalendar(Context context) {
      Date currentDate = new Date();
      // Expiration date one day later
      //AccessToken token = new AccessToken(tokenStr, new Date(currentDate.getTime() + (24 * 60 * 60 * 1000)));
      //GoogleCredentials credential = GoogleCredentials.create(token);
      //GoogleCredential credential = new GoogleCredential().setAccessToken(tokenStr);
      //HttpRequestInitializer requestInitializer = credential;
      // Initialize Calendar service with valid OAuth credentials
      String [] SCOPES = {"https://www.googleapis.com/auth/calendar.readonly"};
      //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
      GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account("freundeskalender.kerim@gmail.com", "klaus"));
      // Calender client
      Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
              .setApplicationName(application_name).build();

      // Retrieve the calendar
      try {
         com.google.api.services.calendar.model.Calendar calendar = service.calendars().get("primary").execute();
         return "ID: "+calendar.getId();
      } catch (UserRecoverableAuthIOException e) {
         ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
         return "NULL";
      } catch (IOException io){
         io.printStackTrace();
         return "NULL";
      }
   }



}