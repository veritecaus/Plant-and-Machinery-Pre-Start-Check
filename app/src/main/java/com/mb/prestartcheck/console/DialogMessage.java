package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DialogMessage extends DialogFragment {

    String title;
    String posMessage;

    public DialogMessage(String t, String p)
    {
        this.title = t;
        this.posMessage = p;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.title)
                .setPositiveButton(this.posMessage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        return builder.create();
    }
}
