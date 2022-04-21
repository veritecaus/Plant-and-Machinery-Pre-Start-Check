package com.mb.prestartcheck.console;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;

import java.util.Date;

public class ViewModelFragmentQuestionAdminOptions extends ViewModelFragmentQuestionAdmin {

    public ViewModelFragmentQuestionAdminOptions(@NonNull Application application, long qid, long sid, boolean isNew) {
        super(application, qid, 3, sid, isNew);

    }


    public void updateQuestion(String title, int seq,  boolean isActive, boolean isCritical, boolean enableMachine,
                               int timeout, String opt1, String opt2, String expectedAnswer,
                               boolean isRandomized,  final String comment, Runnable onUpdated)
    {
        QuestionOptions questionOptions = (QuestionOptions)getQuestion();

        super.updateQuestion(title, 3, seq, isRandomized, "", isActive, isCritical, enableMachine, timeout, expectedAnswer,
                "", "", opt1, opt2, "", comment, onUpdated);

    }

}
