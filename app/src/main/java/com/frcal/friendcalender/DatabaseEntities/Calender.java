package com.frcal.friendcalender.DatabaseEntities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "Calender_table")
public class Calender {
    @PrimaryKey
    @NonNull
    public String calenderID;

    @NonNull
    public String name;
    public String color;

    public Calender(String calenderID, String name, String color){

        // schau ob eine calenderID uebergeben wurde falls ja nimm die, ansonsten erstell eine
        this.calenderID = (calenderID != null) ? calenderID : String.valueOf(UUID.randomUUID());
        this.name = name;
        this.color = color;
    }
}
