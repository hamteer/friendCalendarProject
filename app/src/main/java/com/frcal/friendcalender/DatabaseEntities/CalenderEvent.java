package com.frcal.friendcalender.DatabaseEntities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.frcal.friendcalender.Notifications.NotificationPublisher;
import com.google.api.client.util.DateTime;

import java.util.UUID;

@Entity(tableName = "CalenderEvent_table")
public class CalenderEvent {


    public String calenderID;
    @PrimaryKey
    @NonNull
    public String eventID;
    public String googleEventID;

    @NonNull
    public DateTime startTime;
    //String startTimeZone;
    @NonNull
    public DateTime endTime;
    //String endTimeZone;

    public String description;
    public String summary;
    public String location;
    public String creator;
    public DateTime updated;

    public boolean notificationActive;
    public int notificationID;


    public CalenderEvent(String calenderID, String eventID, String googleEventID,
                         DateTime startTime, DateTime endTime, String description, String summary
            , String location,
                         String creator, DateTime updated, int notificationID) {

        this.calenderID = calenderID;
        // schau ob eine calenderID uebergeben wurde falls ja nimm die, ansonsten erstell eine
        this.eventID = (eventID != null) ? eventID : String.valueOf(UUID.randomUUID());
        this.googleEventID = googleEventID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.summary = summary;
        this.location = location;
        this.creator = creator;
        this.updated = updated;
        this.notificationID = notificationID;
    }

    @Ignore
    @Override
    public String toString() {
        return calenderID + " | " + eventID + " | " + startTime + " | ";
    }

    // needed to easily compare two events to sort them in the ListView
    public String getStartTimeToString() {
        return startTime.toStringRfc3339();
    }

}
