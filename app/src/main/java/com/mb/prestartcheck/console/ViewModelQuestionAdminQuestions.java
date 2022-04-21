package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.QuestionNew;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

public class ViewModelQuestionAdminQuestions extends AndroidViewModel {
    Questions getQuestions() { return this.questions;}

    private Section section;
    private Questions questions = new Questions();

    public ViewModelQuestionAdminQuestions(@NonNull  Application application, long sid) {
        super(application);
        this.section = Sections.getInstance().find(sid);
        //Find all questions for a section from the application singleton.
        sync();
    }

    public Section getSection() { return this.section;}

    public void sync()
    {
        if (this.section == null) return;
        this.questions.clear();
        for(int idx = 0; idx < this.section.getSize(); idx++)
        {
            this.questions.add(this.section.getAt(idx));
        }

       // this.questions.add(QuestionNew.getInstance());
    }


}
