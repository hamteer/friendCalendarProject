package com.frcal.friendcalender.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;


// annotate class as Room Database and inform about entities
@Database(entities = {CalenderEvent.class, Calender.class}, version = 1)
// inform database about needed TypeConverters
@TypeConverters({CalenderEventAttributeTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO taskDao();

}
