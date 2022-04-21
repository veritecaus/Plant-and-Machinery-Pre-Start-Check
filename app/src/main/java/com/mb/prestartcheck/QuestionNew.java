package com.mb.prestartcheck;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.data.TupleQuestionSection;

import java.util.Date;

public class QuestionNew extends Question {

    private static QuestionNew instance;

    public static QuestionNew getInstance()
    {
        if (instance == null) instance = new QuestionNew();

        return instance;
    }

    public QuestionNew()
    {
        this.title = "New Question";
        this.allowMachineOperation = false;
        this.createDateTime = new Date();
        this.updatedDateTime = new Date();
        this.enabled = true;
        this.id = -1;
        this.isCritical = false;
        this.isNegativePositive = false;
        this.sequence = -1;
        this.timeOut = 0;
        this.sectionId = -1;
    }

    @Override
    protected void refreshChild(TupleQuestionSection row) {
            //Elided
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    @Override
    public String getTypeString() {
        return "";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        return true;
    }

    @Override
    public void removeResources() {

    }
}
