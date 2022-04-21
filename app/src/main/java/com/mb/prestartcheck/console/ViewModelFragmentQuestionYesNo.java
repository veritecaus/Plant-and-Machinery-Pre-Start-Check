package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Response;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.data.DaoResponse;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableResponse;

import java.util.Date;

public class ViewModelFragmentQuestionYesNo  extends ViewModelQuestionNavigator {

    private long questionId;

    public ViewModelFragmentQuestionYesNo(@NonNull Application application, long qid) {
        // Set the question and section. Properties "question" and "section "
        //moved down to the base class.
        super(application, Questions.getInstance().find(qid),
                Questioner.getInstance().getQuestionerState().getCurrentSection(),  true);

        this.questionId = qid;
    }

    Questioner getQuestioner() { return Questioner.getInstance();}

    public void recordResponse(boolean yesno, boolean isNegativeQuestion)
    {
        Response response = Questioner.getInstance().getCurrentResponse();
        if (response != null) {
            response.setOperatorResponse(Boolean.toString(yesno));
            response.setIsNegative(isNegativeQuestion);
            Date now = new Date();
            response.setCreatedDateTime(now);
        }
    }

    public  boolean getRecordedResponse()
    {
        Response response = Questioner.getInstance().getCurrentResponse();
        return  response != null && Boolean.parseBoolean(response.getOperatorResponse());
    }





}
