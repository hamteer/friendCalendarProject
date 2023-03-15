package com.frcal.friendcalender.DataAccess;

import android.content.Context;
import android.util.Log;

import com.frcal.friendcalender.room.DatabaseHelper;

import java.util.ArrayList;

import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;



/**
 * Use this Class to manipulate Event-Data in the App
 *
 */
public class EventManager {

    DatabaseHelper db;
    ArrayList<CalenderEvent> events;
    EventManagerListener listener;

    final String logTag = "EventManager";

    public EventManager(Context context, EventManagerListener listener){
        this.db = new DatabaseHelper(context);
        this.listener = listener;
        this.events = db.getAllCalenderEvents();
        Log.d("EventManager", "initialized");
    }

    /**
     * refreshes EventList from database and triggers Listener.onEventListUpdated()
     */
    void requestUpdate(){
        getEvents();
        listener.onEventListUpdated();
    }

    /**
     * Adds event to database
     * @param event event that should be added to the database
     */
    void addEvent(CalenderEvent event){
        // Check if event already exists in database
        for (CalenderEvent eventListElement: this.events) {
            if (eventListElement.eventID.equals(event.eventID)){
                // Element already existing => cant add as new Item has to be updated instead
                Log.d(logTag, "addEvent() Event already existing => updating instead");
                db.updateEvent(event);
                return;
            }
        }
        Log.d(logTag, "addEvent() Adding new Event");
        db.addEvent(event);
        getEvents();
    }

    /**
     * Updates Event in the database
     * @param event event with updated contents
     */
    void updateEvent(CalenderEvent event){
        Log.d(logTag, "updateEvent() Updating existing Event");
        db.updateEvent(event);
        getEvents();
    }

    /**
     * Deletes Event in the database
     * @param event event that will be removed
     */
    void deleteEvent(CalenderEvent event){
        Log.d(logTag, "deleteEvent() Deleting Event");
        db.deleteEvent(event);
        getEvents();
    }

    /**
     * refreshes the CalenderEvents list
     * @return List of CalenderEvents pulled from the database
     */
    ArrayList<CalenderEvent> getEvents(){
        Log.d(logTag, "getEvents() getting Events");
        events = db.getAllCalenderEvents();
        return events;
    }


    public interface EventManagerListener{
        void onEventListUpdated();
    }


}

