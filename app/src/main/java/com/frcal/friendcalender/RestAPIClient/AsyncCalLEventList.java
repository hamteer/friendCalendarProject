package com.frcal.friendcalender.RestAPIClient;

import java.util.List;

public interface AsyncCalLEventList<T> {

    void respGetEventList(List<T> res);

}
