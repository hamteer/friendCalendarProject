package com.frcal.friendcalender.RestAPIClient;

public interface AsyncCalEvent<T> {
    void respGetEvent(T res);
    void respInsertEvent(T res);

    void respDeleteEvent(T res);

    void respUpdateEvent(T res);


}
