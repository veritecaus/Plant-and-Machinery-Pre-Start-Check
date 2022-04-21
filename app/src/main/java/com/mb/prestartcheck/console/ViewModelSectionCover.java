package com.mb.prestartcheck.console;

import android.app.Application;

import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

public class ViewModelSectionCover  extends ViewModelQuestionNavigator {
    final Application application;
    private long sectionId;
    private long questionId;

    public ViewModelSectionCover(Application app, long sid, long qid)
    {
        // Set the question and section. Properties "question" and "section "
        //moved down to the base class.
        super(app, Questions.getInstance().find(qid),
                Sections.getInstance().find(sid));

        application = app;
        sectionId = sid;
        questionId = qid;
    }

    //TODO: Check if these properties are needed.
    public  long getSectionId() { return sectionId;}
    public  long getQuestionId() { return questionId;}

    //TODO: Getters moved to the base class.
    /*
    public Section getSection()
    {
        return Sections.getInstance().find(this.sectionId);
    }

    public Question getQuestion()
    {
        return this.questionId >= 0 ? Questions.getInstance().find(this.questionId)  : null;
    }

     */

    public  Questioner getQuestioner()
    {
        return Questioner.getInstance();
    }

}
