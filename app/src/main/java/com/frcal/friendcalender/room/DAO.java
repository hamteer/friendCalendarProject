package com.frcal.friendcalender.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.frcal.friendcalender.DatabaseEntities.Calender;
import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;

@Dao
public interface DAO {
    @Insert
    void insertEvent(CalenderEvent event);
    @Update
    void updateEvent(CalenderEvent event);
    @Delete
    void deleteEvent(CalenderEvent event);
    @Query("SELECT * from CalenderEvent_table")
    List<CalenderEvent> getAllEvents();

    @Insert
    void insertCalender(Calender calender);
    @Update
    void updateCalender(Calender calender);
    @Delete
    void deleteCalender(Calender calender);
    @Query("SELECT * from Calender_table")
    List<Calender> getAllCalenders();
    @Query("SELECT * from CalenderEvent_table")
    List<CalenderEvent> getAllCalenderEvents();
}
