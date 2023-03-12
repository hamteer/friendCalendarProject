package com.frcal.friendcalender.room;


import androidx.room.TypeConverter;

import com.google.api.client.util.DateTime;

import java.util.Date;

// Class to convert DateTime to long
public class CalenderEventAttributeTypeConverter {

    /*
    TypeConverter zum Konvertieren von DateTime <-> Long
    */
    @TypeConverter
    public static DateTime millisecondsToDate(Long milliseconds) {
        return milliseconds == null ? null : new DateTime(milliseconds);
    }
    @TypeConverter
    public static Long dateToMilliseconds(DateTime date) {
        return date == null ? null : date.getValue();
    }

}
