package com.frcal.friendcalender.RestAPIClient;

import java.util.List;
/**
 * Interface to call the respGetEventList function in the CalenderActivity and pass the google response to it.
 *  * @author Niclas
 */

public interface AsyncCalLEventList<T> {

    void respGetEventList(List<T> res);

}
