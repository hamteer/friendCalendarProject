package com.frcal.friendcalender.Exception;

import android.content.Context;
import android.widget.Toast;

public class InputFormatException extends Exception {
    Context context;

    public InputFormatException(Context context) {
        this.context = context;
    }

    public void notifyUser() {
        Toast.makeText(context, "Bitte Format der Eingaben überprüfen!", Toast.LENGTH_SHORT).show();
    }
}
