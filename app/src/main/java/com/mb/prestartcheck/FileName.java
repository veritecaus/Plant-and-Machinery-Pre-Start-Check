package com.mb.prestartcheck;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileName {

    public final static String DATE_FORMAT = "y_M_d_h_m_s";

    public static String get(User user)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date now = new Date();
        return  String.format("%s_%s_%s.csv", user.getFirstName(), user.getLastName(), simpleDateFormat.format(now));
    }

    public static String get(String prefix)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date now = new Date();
        return  String.format("%s_%s.csv",  prefix, simpleDateFormat.format(now));
    }

    /**
     * Break a string into chars and output to Logcat.
     * @param input : The string to break into characters.
     * @return : nothing.
     */
    public static void tokeniseString(final String input)
    {

        //Create a string of byte integer values seperayed by
        //spaces.
        byte[] bytes = input.getBytes();
        final StringBuilder stringBuilder = new StringBuilder();

        //Show the first 10 characters for referencing.
        if (input.length()>= 10) {
            stringBuilder.append(input.substring(0, 10));
        }else {
            stringBuilder.append(input.substring(0, input.length()));
        }
        stringBuilder.append(": ");
        for (int i = 0; i < bytes.length; i++) {

            stringBuilder.append(bytes[i]);
            stringBuilder.append(' ');
        }

        Log.i(App.TAG, stringBuilder.toString());
    }

}
