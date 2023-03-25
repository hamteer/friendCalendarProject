package com.frcal.friendcalender.RestAPIClient;

import static android.content.Context.MODE_PRIVATE;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.Decorators.EventDecorator;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CalendarEvents extends AsyncTask<Void, Void, String> implements EventManager.EventManagerListener {
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_CALENDAR = 2;
    public AsyncCalEvent delegate = null;
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

    private List<String> attendees = new ArrayList<>();



    /*private List<String> attendees = new LinkedList<>(); */

    Calendar service;
    Integer mtdNr;
    EventManager eventManager;

    //private ArrayList<String> attendees2 = new ArrayList<String>();
// </editor-fold>

    // <editor-fold desc="Konstruktoren">
    //For insert
    public CalendarEvents(Integer mtdNr, Context context, String calendarID, String eventID, String summary, String description, String location, DateTime startTime, DateTime endTime, List<String> attendees) {
        this.mtdNr = mtdNr;
        this.context = context;
        this.calendarID = calendarID;
        this.eventID = eventID;
        this.summary = summary;
        this.description = description;
        this.location = location;


        this.startTime = startTime;
        this.endTime = endTime;


        this.attendees = attendees;


    }


    //For delete and list
    public CalendarEvents(Integer mtdNr, Context context, String calendarID, String eventID) {
        this.mtdNr = mtdNr;
        this.context = context;
        this.calendarID = calendarID;
        this.eventID = eventID;


    }

    //For get
    public CalendarEvents(Integer mtdNr, Context context, String calendarID) {
        this.mtdNr = mtdNr;
        this.calendarID = calendarID;


    }
// </editor-fold>

    @Override
    protected String doInBackground(Void... voids) {
        switch (this.mtdNr) {
            case 1:
                getEvent();
                break;
            case 3:
                setEvent();
                break;
            case 4:
                return deleteEvent();
            case 5:
                updateEvent();
                break;


        }
        return null;

    }

    public void setConfig() {
        String[] SCOPES = {"https://www.googleapis.com/auth/calendar"};
        //SharedPreferences settings = getSharedPreferences(Context.MODE_PRIVATE);
        SharedPreferences sh_clid = context.getSharedPreferences("MainCal-ID", context.MODE_PRIVATE);
        sh_clid.getString("Cal-ID", "");

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES)).setSelectedAccount(new Account(sh_clid.getString("Cal-ID", ""), "klaus"));
        // Calender client
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName(application_name).build();

        this.service = service;


    }

    public void getEvent() {

        try {
            // Retrieve an event
            Event event = service.events().get(this.calendarID, this.eventID).execute();
            JsonFactory jsonEve = event.getFactory();
            Intent jsonIntent = null;
            jsonIntent.putExtra("Calendar", jsonEve.toString());
            ((Activity) context).startActivityForResult(jsonIntent, REQUEST_CALENDAR);

        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    public String setEvent() {
        eventManager = new EventManager(context.getApplicationContext(), this);

        Event event = new Event().setSummary(this.summary).setLocation(this.location).setDescription(this.description);

        EventDateTime start = new EventDateTime().setDateTime(this.startTime);
        event.setStart(start);
        EventDateTime end = new EventDateTime().setDateTime(this.endTime);
        event.setEnd(end);

        if (attendees != null) {
            List<EventAttendee> attendeesToSET = new ArrayList<>();
            for (String email:attendees) {
                    attendeesToSET.add( new EventAttendee().setEmail(email));
            }
            event.setAttendees((attendeesToSET));
        }
        try {
            event = this.service.events().insert(this.calendarID, event).execute();
            JsonFactory jsonSetEvent = event.getFactory();
            CalenderEvent eventDB = new CalenderEvent(this.calendarID, this.eventID, event.getId(), this.startTime, this.endTime, this.description, this.summary, this.location, null, this.endTime,0);
            eventManager.addEvent(eventDB);

            return jsonSetEvent.toString();
        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return "";

    }

    public String deleteEvent() {
        try {
            // Delete Event in Gmail
            this.service.events().delete(this.calendarID, this.eventID).execute();

            return "";
        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException io) {
            return "IOException";
        }
        return "";
    }

    public String updateEvent() {

        try {
            eventManager = new EventManager(context.getApplicationContext(), this);
            // Retrieve the event from the API
            CalenderEvent temp = eventManager.getEventByEventID(this.eventID);
            Event event = service.events().get(this.calendarID, temp.googleEventID).execute();

            // Make a change
            event.setSummary(this.summary).setLocation(this.location).setDescription(this.description);

            EventDateTime start = new EventDateTime().setDateTime(this.startTime);
            event.setStart(start);
            EventDateTime end = new EventDateTime().setDateTime(this.endTime);
            event.setEnd(end);

            if (attendees != null) {
                List<EventAttendee> attendeesToSET = new ArrayList<>();
                for (String i : this.attendees) {
                    attendeesToSET.add( new EventAttendee().setEmail(i));
                }
                event.setAttendees((attendeesToSET));
            }

            // Update the event
            Event updatedEvent = service.events().update(this.calendarID, event.getId(), event).execute();

            CalenderEvent eventDB = new CalenderEvent(this.calendarID, this.eventID, updatedEvent.getId(), this.startTime, this.endTime, this.description, this.summary, this.location, null, this.endTime,1);
            eventManager.addEvent(eventDB);

            JsonFactory jsonSetEvent = event.getFactory();
            return jsonSetEvent.toString();
        } catch (UserRecoverableAuthIOException e) {
            ((Activity) context).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException io) {
            io.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String json) {
        switch (mtdNr) {
            case 1:
                delegate.respGetEvent(json);
            case 3:
                delegate.respInsertEvent(json);
            case 4:
                delegate.respDeleteEvent(json);
            case 5:
                delegate.respUpdateEvent(json);
        }
    }

    @Override
    public void onEventListUpdated() {

        ArrayList<CalenderEvent> eventArrayList = eventManager.getEvents();
        Log.d("CalenderActivity", "onEventListUpdated() called");
        ArrayList<CalendarDay> daysToDecorate = new ArrayList<>();
        for (CalenderEvent event : eventArrayList) {
            Log.d("CalenderActivity", event.startTime.toString());
            String timeString = event.startTime.toString();
            String year = timeString.substring(0, 4);
            String month = timeString.substring(5, 7);
            String day = timeString.substring(8, 10);
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            CalendarDay calendarDay = CalendarDay.from(date);
            daysToDecorate.add(calendarDay);
        }


    }


}
