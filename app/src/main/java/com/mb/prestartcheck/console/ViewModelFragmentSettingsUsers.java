package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.Supervisors;

import org.jetbrains.annotations.NotNull;

public class ViewModelFragmentSettingsUsers extends AndroidViewModel {
    public ViewModelFragmentSettingsUsers(@NonNull @NotNull Application application) {
        super(application);
    }
    public Supervisors getSupervisors() { return Supervisors.getInstance();}
    public Operators getOperators() { return Operators.getInstance();}

}