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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CalendarEvents extends AsyncTask<Void, Void, Void> {
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_CALENDAR = 2;
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String application_name = "My Calendar App";
    private Context context;
// <editor-fold desc="Attributes">
    private String calendarID;
    private String eventID;
    private String summary; // Titel des Termins
    private String location;

    private String description = "No Description";


    private DateTime startTime;
    private DateTime endTime;



    private List<String> attendees = new LinkedList<>();

    Calendar service;
    Integer mtdNr;



    //private ArrayList<String> attendees2 = new ArrayList<String>();
// </editor-fold>

    // <editor-fold desc="Konstruktoren">
    //For insert
    public CalendarEvents(Integer mtdNr, Context context, String calendarID, String eventID, String summary, String description, String location, DateTime startTime, DateTime endTime, List<String> attendees)
    {
        this.mtdNr = mtdNr;
        this.context=context;
        this.calendarID= calendarID;
        this.eventID= eventID;
        this.summary=summary;
        this.description=description;
        this.location=location;


        this.startTime=startTime;
        this.endTime=endTime;


        this.attendees = attendees;


    }
    //For delete and list
    public CalendarEvents(Integer mtdNr, Context context, String calendarID, String eventID)
    {
        this.mtdNr = mtdNr;
        this.context = context;
        this.calendarID= calendarID;
        this.eventID= eventID;



    }
    //For get
    public CalendarEvents(Integer mtdNr, Context context , String calendarID)
    {
        this.mtdNr = mtdNr;
        this.calendarID= calendarID;




    }
// </editor-fold>

    @Override
    protected Void doInBackground(Void... voids) {
        switch (mtdNr) {
            case 1:
                 getEvent();
            case 2:
                 getEventList();
            case 3:
                 setEvent();
            case 4:
                deleteEvent();

        }
     return null;

    }
    public  void setConfig() {
        String [] SCOPES = {"https://www.googleapis.com/auth/calendar"};
        //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account("andoidprojekt1@gmail.com ", "klaus"));
            // Calender client
            Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(application_name).build();

            this.service = service;


    }
    public void getEvent()
    {

        try {
            // Retrieve an event
            Event event = service.events().get(this.calendarID, this.eventID).execute();
            JsonFactory jsonEve = event.getFactory();
            Intent jsonIntent = null;
            jsonIntent.putExtra("Calendar", jsonEve.toString());
            ((Activity) context).startActivityForResult(jsonIntent, REQUEST_CALENDAR);

        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

        } catch(IOException io)
        {
            io.printStackTrace();
        }
    }

    public String getEventList(){

// Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> items;
        try {

            do {
                Events events = this.service.events().list(this.calendarID).setPageToken(pageToken).execute();
                items = events.getItems();

                pageToken = events.getNextPageToken();
            } while (pageToken != null);
            return items.toString();
        }catch(IOException io)
        {
            return io.toString();
        }
    }
    public String setEvent()
    {
        Event event = new Event()
                .setSummary(this.summary)
                .setLocation(this.location)
                .setDescription(this.description);

        EventDateTime start = new EventDateTime()
                .setDateTime(this.startTime)
                .setTimeZone("Europe/Berlin");
        event.setStart(start);
        EventDateTime end = new EventDateTime()
                .setDateTime(this.endTime)
                .setTimeZone("Europe/Berlin");
        event.setEnd(end);

        EventAttendee[] attendees = new EventAttendee[this.attendees.size()];
        for (String i : this.attendees) {
            new EventAttendee().setEmail(i);
        }
        event.setAttendees(Arrays.asList(attendees));
        try {
            event = this.service.events().insert(this.calendarID, event).execute();
            JsonFactory jsonSetEvent = event.getFactory();
            return event.toString();
        }catch(IOException io)
        {
            return io.toString();

        }

    }
    public String deleteEvent( )
    {
        try {
            // Retrieve an event
            Event event = this.service.events().get(this.calendarID, this.eventID).execute();
            return event.toString();
        }catch(IOException io)
        {
            return io.toString();
        }
    }
    /*
    public String updateEvent(Context context)
    {

        try {
            // Retrieve an event
            Event event = service.events().get(this.calendarID, this.eventID).execute();
            if(this.summary)

            Event updatedEvent = service.events().update(calendarID, event.getId(), event).execute();
            return event.toString();
        }catch(IOException io)
        {
            return io.toString();
        }
    }
    */


}