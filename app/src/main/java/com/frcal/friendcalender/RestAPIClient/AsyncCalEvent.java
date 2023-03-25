package com.frcal.friendcalender.RestAPIClient;

/**
 * Interface to call the  functions in the AddDateActivity and pass the google response to it.
 *  * @author Niclas
 */

public interface AsyncCalEvent<T> {
    void respGetEvent(T res);
    void respInsertEvent(T res);

    void respDeleteEvent(T res);

    void respUpdateEvent(T res);


}
