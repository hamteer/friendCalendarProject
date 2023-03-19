package com.frcal.friendcalender.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;


// Klasse als RoomDatenbank markieren und über die Entitäten informieren
@Database(entities = {CalenderEvent.class, Calender.class}, version = 1)
// Datenbank über benötigte TypeConverters informieren
@TypeConverters({CalenderEventAttributeTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // DAO ist eine Klasse, die mit @Dao annotiert wurde
    public abstract DAO taskDao();

}
