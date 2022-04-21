package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class DialogQuestion extends DialogFragment {


    private String title;
    private String posMessage;
    private String negMessage;
    private DialogFragmentListener listener;
    private final Fragment owner;

    public DialogQuestion(String t, String p, String negMsg, Fragment fragment)
    {
        this.title = t;
        this.posMessage = p;
        this.negMessage = negMsg;
        this.owner = fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.title)
                .setPositiveButton(this.posMessage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (listener != null) listener.onPositive(DialogQuestion.this, DialogQuestion.this.owner);
                        dialog.dismiss();
                    }
                });

        if (!this.negMessage.isEmpty())
                builder.setNegativeButton(this.negMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) listener.onNegative(DialogQuestion.this, DialogQuestion.this.owner);
                        dialog.dismiss();
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            listener = (DialogFragmentListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
