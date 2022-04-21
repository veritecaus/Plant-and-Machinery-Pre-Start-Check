package com.mb.prestartcheck.console;

import android.app.Application;

import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Response;
import com.mb.prestartcheck.Section;

import java.util.Date;

public class ViewModelFragmentQuestionMultipleChoice extends ViewModelQuestionNavigator {

    private long questionId;

    public ViewModelFragmentQuestionMultipleChoice(Application app, long qid) {
        // Set the question and section. Properties "question" and "section "
        //moved down to the base class.
        super(app, Questions.getInstance().find(qid),
                Questioner.getInstance().getQuestionerState().getCurrentSection(), true);
        //Yes, show the info button in the navigation bar.
        this.showSectionInfo = true;

        this.questionId = qid;

    }

    public Question getQuestion() {
        return this.question;
    }
    public Section getSection()
    {
        return this.section;
    }

    public void recordResponse(String choice)
    {
        Response response = Questioner.getInstance().getCurrentResponse();
        if (response !=null )
        {
            response.setCreatedDateTime(new Date());
            response.setOperatorResponse(choice);
        }

    }


}
