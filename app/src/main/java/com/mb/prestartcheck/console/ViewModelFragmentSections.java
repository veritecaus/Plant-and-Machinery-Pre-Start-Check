package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

public class ViewModelFragmentSections extends AndroidViewModel  {

    private Sections sections;

    public ViewModelFragmentSections(@NonNull Application application) {
        super(application);
        this.sections = new Sections();

    }

    public Sections getSections() { return sections;}
    public Sections getSectionsInstance() { return Sections.getInstance();}

    public void sync()
    {
        this.sections.clear();
        Sections src = Sections.getInstance();

        for(int idx = 0; idx <  Sections.getInstance().getSize(); idx++)
            this.sections.add(src.getAt(idx));

        //Add a section for new items.
        //this.sections.add(Section.getNewItem());



    }

}
