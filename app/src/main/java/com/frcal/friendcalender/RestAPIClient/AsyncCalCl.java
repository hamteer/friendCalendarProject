package com.frcal.friendcalender.RestAPIClient;

public interface AsyncCalCl<T> {
    void respCalendar(T res);
    void respInsertCal(T res);
}
