package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.R;

/**
 *  Display a dialog on top of an activity. User can close the dialog
 *  by tapping on the positive button or tapping the background activity.
 */
public class AlertManager {

    public static void showDialog(Context cx, int msgResId)
    {
        Dialog dialog =  new AlertDialog.Builder(cx)
                .setMessage(msgResId)
                .setPositiveButton(R.string.ok, null)
                .create();
        dialog.show();
    }

    /**
     * Display a text message in a  dialog and give the postive button a specific label.
     * @param cx Context from a fragment or activity.
     * @param message Text message to display.
     * @param positiveButtonResId String resource identifier for the text displayed on the positive button.
     */
    public static void showDialog(Context cx, @NonNull final String message, @NonNull final int  positiveButtonResId)
    {
        /**
         *  Use Android's "AlertDialog" builder class to create a
         *  text message dialog box with a positive button that
         * clases the dialog.
         */
        Dialog dialog =  new AlertDialog.Builder(cx)
                .setMessage(message)
                .setPositiveButton(positiveButtonResId, null)
                .create();
        dialog.show();
    }

}
