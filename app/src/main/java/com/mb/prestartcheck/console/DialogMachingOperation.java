package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.R;

public class DialogMachingOperation extends DialogFragment {


    private boolean dismissed;
    private final FragmentQuestion owner;

    DialogFragmentListener listener;

    public DialogMachingOperation(FragmentQuestion e)
    {
        this.owner = e;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.alert_machine_operation)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(App.TAG, "Ok pressed.");
                        dismissed = false;
                        listener.onPositive(DialogMachingOperation.this, DialogMachingOperation.this.owner);
                    }
                })
                .setCancelable(false);
        // Create the AlertDialog object and return it
        dismissed = true;
        return builder.create();
    }


    /**
     * A fragment manager is attached when the dialog is displayed.
     * A transaction is made to add the this DialogFragment and then
     * "onAttach" is called in the process.
     * @param context Owner of the dialog that implements DialogFragmentListener.
     */
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

    @Override
    public void onStop() {
        if (dismissed)  listener.onNegative(this, this.owner);
        super.onStop();
    }
}
