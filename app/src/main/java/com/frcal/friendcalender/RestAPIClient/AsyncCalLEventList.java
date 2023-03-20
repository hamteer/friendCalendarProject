package com.frcal.friendcalender.RestAPIClient;

public interface AsyncCalLEventList<T> {
    void respListCalList(T res);
    void respInsertCalList(T res);
    void respGetCalList(T res);
}
