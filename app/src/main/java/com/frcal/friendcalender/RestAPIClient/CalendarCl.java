package com.frcal.friendcalender.RestAPIClient;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CalendarCl extends AsyncTask<Void, Void, String> {
   // Schnittstelle zu AsyncTask
   public AsyncCalCl delegate = null;
   private static final HttpTransport httpTransport = new NetHttpTransport();
   private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
   ;
   private static final String application_name = "My Calendar App";
   private String calendarId;

   private Context context;

   // Funktion auswählen 1=getCalender
   private int mtdNr; //1=listCalender;
   private static final int REQUEST_AUTHORIZATION = 1;
   private static final int REQUEST_CALENDAR = 2;
   private  Calendar service;
   public CalendarCl(int mtdNr, Context context) {
      this.context = context;
      this.mtdNr = mtdNr;
   }

   public CalendarCl(int mtdNr, Context context, String calendarId) {
      this.context = context;
      this.mtdNr = mtdNr;
      this.calendarId = calendarId;
   }
   public void setService() {
      String [] SCOPES = {"https://www.googleapis.com/auth/calendar"};
              //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
              GoogleAccountCredential cred = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account(calendarId, "klaus"));
      // Calender client
      Calendar service = new Calendar.Builder(httpTransport, jsonFactory, cred)
              .setApplicationName(application_name).build();
       this.service = service;
    }
   @Override
   protected String doInBackground(Void... voids) {
      switch (mtdNr) {
         case 1:
            return getCalendar(context, calendarId);
         case 2:
            return insertSecCalendar(context, calendarId);
      }
      return null;
   }

   public String getCalendar(Context context, String calendarId) {

      setService();
      JsonFactory jsonFactory = new JacksonFactory();
      try {
         com.google.api.services.calendar.model.Calendar calendar = service.calendars().get(calendarId).execute();
         // Konvertiere das Calendar-Objekt in ein JSON-String
         String jsonString = jsonFactory.toPrettyString(calendar);
         // Konvertiere den JSON-String in ein Calendar-Objekt
         // com.google.api.services.calendar.model.Calendar calendarFromJson = jsonFactory.fromString(jsonString, com.google.api.services.calendar.model.Calendar.class);

         return jsonString;


      } catch (UserRecoverableAuthIOException e) {
         ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
      } catch (IOException io) {
         io.printStackTrace();
      }
      return "";
   }

   public String insertSecCalendar(Context context, String calendarName) {
      if (calendarName==null) {
         return "";
      }
      setService();
      // Create a new calendar
      com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
      calendar.setSummary(calendarName);
      calendar.setTimeZone("Europe/Berlin");
      // Insert the new calendar
      com.google.api.services.calendar.model.Calendar createdCalendar = null;
      try {
         createdCalendar = service.calendars().insert(calendar).execute();
         // Konvertiere das Calendar-Objekt in ein JSON-String
         String jsonString = jsonFactory.toPrettyString(createdCalendar);
         return jsonString;

      } catch (IOException e) {
         return  e.toString();
      }

   }
   @Override
   protected void onPostExecute(String json) {
      switch (mtdNr) {
         case 1:
            delegate.respCalendar(json);
         case 2:
            delegate.respInsertCal(json);
      }
   }
}
