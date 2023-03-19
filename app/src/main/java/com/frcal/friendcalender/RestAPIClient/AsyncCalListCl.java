package com.frcal.friendcalender.RestAPIClient;

public interface AsyncCalListCl<T> {
    void respListCalList(T res);
    void respInsertCalList(T res);
    void respGetCalList(T res);
}
