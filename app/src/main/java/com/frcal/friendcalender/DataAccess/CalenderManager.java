package com.frcal.friendcalender.DataAccess;

import android.content.Context;
import android.util.Log;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.room.DatabaseHelper;

import java.util.ArrayList;

import DatabaseEntities.Calender;


/**
 * Use this Class to manipulate Calender-Data in the App
 * @author Daniel
 *
 */
public class CalenderManager {

    DatabaseHelper db;
    ArrayList <Calender> calenders;
    CalenderManagerListener listener;
    final String logTag = "CalenderManager";

    public CalenderManager(Context context, CalenderManagerListener listener){
        this.db = new DatabaseHelper(context);
        this.listener = listener;
        this.calenders = db.getAllCalenders();
    }

    /**
     * refreshes CalenderList from database and triggers Listener.onEventListUpdated()
     */
    public void requestUpdate(){
        getCalenders();
        listener.onCalenderListUpdated();
    }

    /**
     * Adds calender to database
     * @param calender calender that should be added to the database
     */
    public void addCalender(Calender calender){
        // Check if calender already exists in database
        for (Calender calenderListElement: this.calenders) {
            if (calenderListElement.calenderID.equals(calender.calenderID)){
                // Element already existing => cant add as new Item has to be updated instead
                Log.d(logTag, "addCalender() Calender already existing => updating instead");
                db.updateCalender(calender);
                return;
            }
        }
        Log.d(logTag, "addCalender() Adding new Calender");
        db.addCalender(calender);
        requestUpdate();
    }
    /**
     * Updates Calender in the database
     * @param calender calender with updated contents
     */
    public void updateCalender(Calender calender){
        Log.d(logTag, "updateCalender() Updating existing Event");
        db.updateCalender(calender);
        requestUpdate();
    }
    /**
     * Deletes Calender in the database
     * @param calender calender that will be removed
     */
    public void deleteCalender(Calender calender){
        Log.d(logTag, "deleteCalender() Deleting Calender");
        db.deleteCalender(calender);
        requestUpdate();
    }

    /**
     * refreshes the Calenders list
     * @return List of Calenders pulled from the database
     */
    public ArrayList<Calender> getCalenders(){
        Log.d(logTag,"getCalenders() getting Calenders");
        calenders = db.getAllCalenders();
        return calenders;
    }

    public interface CalenderManagerListener{
        void onCalenderListUpdated();
    }

}
