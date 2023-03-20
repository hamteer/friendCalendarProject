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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CalendarEventList extends AsyncTask<Void, Void, List<String>> {
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_CALENDAR = 2;
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String application_name = "My Calendar App";

    public AsyncCalLEventList delegate = null;
    private Context context;
    // <editor-fold desc="Attributes">
    private String calendarID;

    Calendar service;
    Integer mtdNr;


    @Override
    protected List<String> doInBackground(Void... voids) {
        switch (this.mtdNr) {
            case 2:
                getEventList();
                break;


        }
        return null;
    }

    public CalendarEventList(Integer mtdNr, Context context, String calendarID) {
        this.mtdNr = mtdNr;
        this.context = context;
        this.calendarID = calendarID;


    }

    public void setConfig() {
        String[] SCOPES = {"https://www.googleapis.com/auth/calendar"};
        //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account("andoidprojekt1@gmail.com ", "klaus"));
        // Calender client
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName(application_name).build();

        this.service = service;


    }


    public List<String> getEventList() {

// Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> items;
        List<Event> allEvents = new ArrayList<>();
        List<String> jsonResponses = new ArrayList<>();
        try {

            do {
                Events events = this.service.events().list(this.calendarID).setPageToken(pageToken).execute();
                items = events.getItems();
                //allEvents.addAll(items);
                pageToken = events.getNextPageToken();
                jsonResponses.add(events.toPrettyString());
            } while (pageToken != null);
            return jsonResponses;
        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(List<String> json) {
        switch (mtdNr) {
            case 2:
                delegate.respGetEventList(json);

        }
    }
}
