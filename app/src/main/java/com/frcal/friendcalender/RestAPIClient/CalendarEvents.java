package com.frcal.friendcalender.RestAPIClient;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;


import com.frcal.friendcalender.DataAccess.EventManager;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;

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

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
/**
 * Use this Class to get, delete, set and update Calender-Events from the googlecalendar
 *  * @author Niclas
 */

// <editor-fold desc="Description">
/*
This code is an Android class named CalendarEvents that extends AsyncTask. It manages the interaction with Google Calendar API to perform CRUD (create, read, update, delete) operations on calendar events.

The class has several attributes, some of which include calendarID, eventID, summary, location, description, startTime, endTime, and attendees. These attributes hold the necessary information for creating, reading, updating, and deleting events on the calendar.

The class has several methods, including doInBackground(), setConfig(), getEvent(), setEvent(), deleteEvent(), and updateEvent(). The doInBackground() method is called when the execute() method of the AsyncTask is called. It takes a variable number of void arguments and returns a string. The method determines the method number (mtdNr) that was passed to the constructor and then calls the appropriate method to perform the corresponding CRUD operation on the calendar events.

The setConfig() method sets up the necessary configuration to access the Google Calendar API. It uses the context of the activity to obtain the user's Google account and sets up an HttpTransport and JsonFactory instance.

The getEvent() method retrieves an event from the calendar with the specified calendarID and eventID. If the event is found, it is converted to a JSON string and passed to another activity via an Intent. If a user authorization error occurs, it starts the authorization process by calling the startActivityForResult() method.

The setEvent() method creates a new calendar event with the specified details and adds the event to the calendar. If any attendees are specified, the attendees are added to the event. If the operation is successful, the method returns a JSON string of the newly created event.

The deleteEvent() method deletes a specified event from the calendar. If the operation is successful, the method returns an empty string.

The updateEvent() method updates a specified event on the calendar with the specified details. If the operation is successful, the method returns a JSON string of the updated event.
*/
// </editor-fold>
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
    private String summary="Kein Titel";
    private String location ="Kein Ort";

    private String description = "Keine Beschreibung";


    private DateTime startTime;
    private DateTime endTime;

    private List<String> attendees = new ArrayList<>();

    Calendar service;
    Integer mtdNr;
    EventManager eventManager;

// </editor-fold>

    // <editor-fold desc="Constructors">
    //For insert and update
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


    //For delete
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

        if (attendees != null) { //add all attendees
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
        //Log.d("CalenderActivity", "onEventListUpdated() called");
        ArrayList<CalendarDay> daysToDecorate = new ArrayList<>();
        for (CalenderEvent event : eventArrayList) {
            //Log.d("CalenderActivity", event.startTime.toString());
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
