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
        // Erstellen der Datenbank; benötigt werden Kontext, Klasse der Datenbank, die man erstellen will und der Name der Datenbank
        db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                // ermöglicht das Ausführen von Datenbankabfragen im MainThread (wird nicht empfohlen!)
                .allowMainThreadQueries()
                .build();
    }

    // einzelnes Event in die Datenbank aufnehmen
    public void addEvent(CalenderEvent event) {
        db.taskDao().insertEvent(event);
    }

    // bestehendes Event in der Datenbank updaten
    public void updateEvent(CalenderEvent event) {
        db.taskDao().updateEvent(event);
    }

    // bestehendes Event in der Datenbank löschen
    public void deleteEvent(CalenderEvent event){
        db.taskDao().deleteEvent(event);
    }

    // einzelnen Calender zur Datenbank hinzufügen
    public void addCalender(Calender calender) {
        try{
            db.taskDao().insertCalender(calender);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // bestehenden Calender in der Datenbank updaten
    public void updateCalender(Calender calender) {
        db.taskDao().updateCalender(calender);
    }

    // bestehenden Calender in der Datenbank löschen
    public void deleteCalender(Calender calender){
        db.taskDao().deleteCalender(calender);
    }
    // alle in der Datenbank existierenden Calenders holen
    public ArrayList<CalenderEvent> getAllCalenderEvents() {
        return new ArrayList<CalenderEvent>(db.taskDao().getAllCalenderEvents());
    }
    // alle in der Datenbank existierenden Calenders holen
    public ArrayList<Calender> getAllCalenders() {
        return new ArrayList<Calender>(db.taskDao().getAllCalenders());
    }
}

