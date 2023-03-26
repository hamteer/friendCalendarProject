package com.frcal.friendcalender.room;

import android.content.Context;
import androidx.room.Room;
import java.util.ArrayList;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;

public class DatabaseHelper {

    private static final String DATABASE_NAME = "Calender-CalenderEvents-db";
    private final Context context;
    private AppDatabase db;

    public DatabaseHelper(Context context) {
        this.context = context;
        initDatabase();
    }

    private void initDatabase() {
        // Create database: we need context, database class and database name
        db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    // add single event to database
    public void addEvent(CalenderEvent event) {
        db.taskDao().insertEvent(event);
    }

    // update event in database
    public void updateEvent(CalenderEvent event) {
        db.taskDao().updateEvent(event);
    }

    // delete existing event from database
    public void deleteEvent(CalenderEvent event){
        db.taskDao().deleteEvent(event);
    }

    // add calendar to database
    public void addCalender(Calender calender) {
        try{
            db.taskDao().insertCalender(calender);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // update calendar in database
    public void updateCalender(Calender calender) {
        db.taskDao().updateCalender(calender);
    }

    // delete existing calendar from database
    public void deleteCalender(Calender calender){
        db.taskDao().deleteCalender(calender);
    }
    // get all existing events from database
    public ArrayList<CalenderEvent> getAllCalenderEvents() {
        return new ArrayList<CalenderEvent>(db.taskDao().getAllCalenderEvents());
    }
    // get all existing calendars from database
    public ArrayList<Calender> getAllCalenders() {
        return new ArrayList<Calender>(db.taskDao().getAllCalenders());
    }
}

