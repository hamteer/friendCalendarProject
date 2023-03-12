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
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CalendarListCl extends AsyncTask<Void, Void, Void> {

   private static final HttpTransport httpTransport = new NetHttpTransport();
   private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
   ;
   private static final String application_name = "My Calendar App";
   private String tokenStr, calendarId;

   private Context context;

   // Funktion auswählen 1=getCalender
   private int mtdNr; //1=listCalender;
   private static final int REQUEST_AUTHORIZATION = 1;
   private static final int REQUEST_CALENDAR = 2;
   private  Calendar service;
   public CalendarListCl(int mtdNr, Context context, String calendarId) {
      this.context = context;
      this.mtdNr = mtdNr;
      this.calendarId = calendarId;
   }
   public void setService(Context context) {
      String [] SCOPES = {"https://www.googleapis.com/auth/calendar"};
              //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
              GoogleAccountCredential cred = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account(calendarId, "klaus"));
      // Calender client
      Calendar service = new Calendar.Builder(httpTransport, jsonFactory, cred)
              .setApplicationName(application_name).build();
       this.service = service;
    }
   @Override
   protected Void doInBackground(Void... voids) {
      switch (mtdNr) {
         case 1:
            listCalendar(context);
         case 2:
            insertCalendar(context, "Hans Peter Klausens");
         case 3:
            getCalendar(context, calendarId);
      }
      return null;
   }

   public JsonFactory listCalendar(Context context) {

      setService(context);
      // Iterate through entries in calendar list
      String pageToken = null;
      do {
         CalendarList calendarList = null;
         try {
            calendarList = service.calendarList().list().setPageToken(pageToken).execute();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }

         List<CalendarListEntry> items = calendarList.getItems();

         for (CalendarListEntry calendarListEntry : items) {
            System.out.println(calendarListEntry.getSummary());
         }
         pageToken = calendarList.getNextPageToken();
      } while (pageToken != null);
      return null;
   }
   public JsonFactory getCalendar(Context context, String calendarId) {

      setService(context);
      // Retrieve a specific calendar list entry
      CalendarListEntry calendarListEntry = null;
      try {
         calendarListEntry = service.calendarList().get(calendarId).execute();
         JsonFactory jsonCal = calendarListEntry.getFactory();
         Intent jsonIntent = new Intent(context, context.getClass());
         jsonIntent.putExtra("CalendarList", jsonCal.toString());
      } catch (UserRecoverableAuthIOException e) {
         ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
         return jsonFactory;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return jsonFactory;

   }
   public JsonFactory insertCalendar(Context context, String newCalendarID) {
      if (calendarId==null) {
         return jsonFactory;
      }
      setService(context);
      // Create a new calendar list entry
      CalendarListEntry calendarListEntry = new CalendarListEntry();
      calendarListEntry.setId(newCalendarID);

      // Insert the new calendar list entry
      CalendarListEntry createdCalendarListEntry = null;
      try {
         createdCalendarListEntry = service.calendarList().insert(calendarListEntry).execute();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      System.out.println(createdCalendarListEntry.getSummary());
      return jsonFactory;
   }

}
