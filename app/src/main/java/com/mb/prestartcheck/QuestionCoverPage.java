package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TupleQuestionSection;

public class QuestionCoverPage  extends Question{

    static QuestionCoverPage instance;

    public  QuestionCoverPage()
    {
        this.id = -1;
        this.title = "";
        this.sequence = -1;
    }


    public static  QuestionCoverPage getInstance()
    {
        if (instance == null) instance = new QuestionCoverPage();
        return instance;
    }
    @Override
    protected void refreshChild(TupleQuestionSection row) {
        this.id = -1;
        this.title = "";
        this.sequence = -1;
    }

    @Override
    public String getTypeString() {
        return "Cover page";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        return true;
    }

    @Override
    public void removeResources() {

    }
}
