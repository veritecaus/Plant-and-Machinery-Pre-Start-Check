package com.mb.prestartcheck.console;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public interface DialogFragmentListener {
    void onPositive(DialogFragment dialog, Fragment fragment);
    void onNegative(DialogFragment dialog, Fragment fragment);
}
