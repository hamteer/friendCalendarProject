package com.frcal.friendcalender.RestAPIClient;

import static android.content.Context.MODE_PRIVATE;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Use this Class to get all Calender-Events from the googlecalendar
 *
 * @author Niclas
 */
public class CalendarEventList extends AsyncTask<Void, Void, List<Event>> {
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_CALENDAR = 2;
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String application_name = "My Calendar App";

    public AsyncCalLEventList delegate = null;
    private Context context;
    private String calendarID;

    Calendar service;
    Integer mtdNr;


    @Override
    protected List<Event> doInBackground(Void... voids) { //Method to execute the other methods asynchronous
        switch (this.mtdNr) {
            case 2:
                return getEventList();


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
        SharedPreferences sh_clid = context.getSharedPreferences("MainCal-ID", MODE_PRIVATE);
        sh_clid.getString("Cal-ID", "");

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account(sh_clid.getString("Cal-ID", ""), "klaus"));
        // Calender client
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName(application_name).build();

        this.service = service;


    }


    public List<Event> getEventList() {

        // Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> items;
        List<Event> allEvents = new ArrayList<>();
        try {

            do {
                Events events = this.service.events().list(this.calendarID).setPageToken(pageToken).execute();
                items = events.getItems();   //get all events of one page
                allEvents.addAll(items); //add the events to a list
                pageToken = events.getNextPageToken();
            } while (pageToken != null); //the max amount of events of one page is 250
            return allEvents;
        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Event> json) { //to call the interface and parse the google events in the CalendarActivity
        switch (mtdNr) {
            case 2:
                delegate.respGetEventList(json);

        }
    }
}
