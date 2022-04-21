package com.mb.prestartcheck.console;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;

import java.util.Date;

public class ViewModelFragmentQuestionAdminMul extends ViewModelFragmentQuestionAdmin {


    public ViewModelFragmentQuestionAdminMul(@NonNull Application application, long qid, long sid, boolean isNew) {
        super(application, qid, 2, sid, isNew);

    }

    public void updateQuestion(String title, int seq,  boolean isActive, boolean isCritical, boolean enableMachine,
                               int timeout, String field1, String expectedAns, boolean isRandomized,final String comment,
                               Runnable onUpdated)
    {

        super.updateQuestion(title, 2, seq, isRandomized, "", isActive, isCritical, enableMachine, timeout, expectedAns,
                "",field1, "", "", "", comment, onUpdated);

    }


}
