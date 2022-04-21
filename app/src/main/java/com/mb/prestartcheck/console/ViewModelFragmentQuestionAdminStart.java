package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.Sections;

public class ViewModelFragmentQuestionAdminStart extends AndroidViewModel  {
    public ViewModelFragmentQuestionAdminStart(@NonNull  Application application) {
        super(application);
    }

    public Sections getSections() { return Sections.getInstance();}
}
