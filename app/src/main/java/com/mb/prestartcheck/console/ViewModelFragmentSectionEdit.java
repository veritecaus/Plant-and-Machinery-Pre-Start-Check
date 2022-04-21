package com.mb.prestartcheck.console;

import android.app.Application;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.DaoSection;
import com.mb.prestartcheck.data.DaoSectionQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;

import java.util.ArrayList;
import java.util.Date;

public class ViewModelFragmentSectionEdit extends AndroidViewModel {
    private final Section section;

    public ViewModelFragmentSectionEdit(@NonNull  Application application, long s) {
        super(application);
        if (s > 0) this.section = Sections.getInstance().find(s);
        else {
            int nextseq = Sections.getInstance().nextSequence();
            this.section = new Section(0, "", "", true, nextseq, false, true);
            this.section.setCreatedDatetime(new Date());
        }


    }

    public  Section getSection() { return section;}

    public void setSection(String title, String desc, int number, boolean showCover, boolean randomize, boolean active,
                              Runnable oncomplete)
    {
        this.section.setTitle(title);
        this.section.setDescription(desc);
        this.section.setShowCoverPage(showCover);
        this.section.setRandomQuestions(randomize);
        this.section.setEnabled(active);
        this.section.setSequence(number);
        //Insert
        PrestartCheckDatabase db = PrestartCheckDatabase.getDatabase(this.getApplication());
        DaoSection dao = db.getDaoSection();
        db.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //Observer is called.
                if (ViewModelFragmentSectionEdit.this.section.getId() == 0)
                    dao.Insert(Section.toTable(ViewModelFragmentSectionEdit.this.section, false));
                else
                    dao.Update(Section.toTable(ViewModelFragmentSectionEdit.this.section, false));
                oncomplete.run();
            }
        });

    }

    public void delete ( Runnable oncomplete)
    {
        PrestartCheckDatabase db = PrestartCheckDatabase.getDatabase(this.getApplication());
        DaoSection dao = db.getDaoSection();
        DaoQuestion daoQuestion = db.getDaoQuestion();
        DaoSectionQuestion daoSectionQuestion = db.getDaoSectionQuestion();

        db.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //Observer is called.
                if (section.getId() > 0) {
                    //Delete all questions in the section
                    //Delete relationships
                    ArrayList<Long> qids = new ArrayList<Long>();
                    for(int idx = 0 ; idx < section.getSize();idx++)
                        qids.add(section.getAt(idx).getId());
                    Date now = new Date();
                    daoQuestion.deleteIds(qids, now.getTime());
                    daoSectionQuestion.delete(section.getId(), now.getTime());
                    dao.delete(section.getId(), now.getTime());
                }
                oncomplete.run();
            }
        });

    }

}
