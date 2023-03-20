package com.frcal.friendcalender.RestAPIClient;

public interface AsyncCalEvent<T> {
    void respCalendar(T res);
    void respInsertCal(T res);
}
