package com.frcal.friendcalender.RestAPIClient;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
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
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarListCl extends AsyncTask<Void, Void, String> {

   public AsyncCalListCl delegate = null;
   private static final HttpTransport httpTransport = new NetHttpTransport();
   private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
   ;
   private static final String application_name = "My Calendar App";
   private String tokenStr, calendarId;

   private Context context;

   // Funktion auswählen 1=getCalender
   private int mtdNr; //1=listCalender;
   private static final int REQUEST_AUTHORIZATION = 1;
   private  Calendar service;
   public CalendarListCl(int mtdNr, Context context) {
      this.context = context;
      this.mtdNr = mtdNr;
   }
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
   protected String doInBackground(Void... voids) {
      switch (mtdNr) {
         case 1:
            return listCalendar(context);
         case 2:
            // in diesem Fall Calendar-ID=new Calendar Name
            return insertCalendar(context, calendarId);
         case 3:
            return getCalendar(context, calendarId);
      }
      return null;
   }

   public String listCalendar(Context context) {

      setService(context);
      List<CalendarListEntry> result = new ArrayList<>();
      // Iterate through entries in calendar list
      String pageToken = null;
      do {
         CalendarList calendarList = null;
         try {
            calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
               result.add(calendarListEntry);
            }
            pageToken = calendarList.getNextPageToken();
         } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } while (pageToken != null);
      String json = new Gson().toJson(result);
      return json;
   }
   public String getCalendar(Context context, String calendarId) {

      setService(context);
      // Retrieve a specific calendar list entry
      CalendarListEntry calendarListEntry = null;
      try {
         calendarListEntry = service.calendarList().get(calendarId).execute();
         String json = new Gson().toJson(calendarListEntry);
         return json;
      } catch (UserRecoverableAuthIOException e) {
         ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "";
   }
   public String insertCalendar(Context context, String newCalendarID) {
      if (calendarId==null) {
         return "";
      }
      setService(context);
      // Create a new calendar list entry
      CalendarListEntry calendarListEntry = new CalendarListEntry();
      calendarListEntry.setId(newCalendarID);

      // Insert the new calendar list entry
      CalendarListEntry createdCalendarListEntry = null;
      try {
         createdCalendarListEntry = service.calendarList().insert(calendarListEntry).execute();
         String json = new Gson().toJson(createdCalendarListEntry);
         return json;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "";
   }
   // After asynctask
   @Override
   protected void onPostExecute(String json) {
      switch (mtdNr) {
         case 1:
            delegate.respListCalList(json);
         case 2:
            delegate.respInsertCalList(json);
         case 3:
            delegate.respGetCalList(json);
      }
   }
}
