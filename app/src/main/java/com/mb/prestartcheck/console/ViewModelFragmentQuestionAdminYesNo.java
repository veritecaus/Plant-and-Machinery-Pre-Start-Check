package com.mb.prestartcheck.console;

import android.app.Application;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.FactoryQuestion;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.DaoSectionQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TableSectionQuestion;

import java.util.Date;

public class ViewModelFragmentQuestionAdminYesNo extends ViewModelFragmentQuestionAdmin {

    public  Question modifiedQuestion =getQuestion();

    public ViewModelFragmentQuestionAdminYesNo(@NonNull Application application, long qid, long sid, boolean isnew) {
        super(application, qid, 1, sid, isnew);

    }

    public void updateQuestion(String title, int seq,  boolean posneg, String altText, boolean isActive,
                               boolean isCritical, boolean enableMachine, int timeout, String expectedAnswer,
                               String expectedAnswerNeg, final String comment, final String altComment,  Runnable onUpdated)
    {
        super.updateQuestion(title, 1, seq, posneg, altText, isActive, isCritical, enableMachine, timeout, expectedAnswer,
                expectedAnswerNeg, "", altComment, "", "", comment, onUpdated);
         modifiedQuestion.setTitle(title);
         modifiedQuestion.setSequence(seq);
         modifiedQuestion.setIsNegativePositive(posneg);
         modifiedQuestion.setTitleAlternative(altText);
         modifiedQuestion.setIsCritical(isCritical);
         modifiedQuestion.setAllowMachineOperation(enableMachine);
          modifiedQuestion.setTimeout(timeout);
         modifiedQuestion.setExpectedAnswer(expectedAnswer);
         modifiedQuestion.setExpectedAnswerNeg(expectedAnswerNeg);
         modifiedQuestion.setComment(comment);
         modifiedQuestion.setAltComment(altComment);

    }

}
