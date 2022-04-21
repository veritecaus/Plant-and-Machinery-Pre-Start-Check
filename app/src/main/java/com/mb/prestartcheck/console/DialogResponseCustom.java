package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mb.prestartcheck.R;


public class DialogResponseCustom extends DialogFragment {


    private View view;
    private DialogFragmentListener listener;
    private final Fragment owner;
    private String response;

    public String getResponse() { return this.response;}

    public  DialogResponseCustom(Fragment owner)
    {
        this.owner = owner;
    }

    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.view_option_custom_title);

        view = getActivity().getLayoutInflater().inflate(R.layout.view_custom_response,null);

        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText editText =  DialogResponseCustom.this.view.findViewById(R.id.editTextViewOptionCustom);
                response = editText.getText().toString();
                if (listener != null) listener.onPositive(DialogResponseCustom.this, DialogResponseCustom.this.owner);
                dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (listener != null) listener.onNegative(DialogResponseCustom.this,  DialogResponseCustom.this.owner);
                dismiss();

            }
        });




        return builder.create();
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            listener = (DialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()  + " must implement DialogResponseCustomListener");
        }
    }
}
