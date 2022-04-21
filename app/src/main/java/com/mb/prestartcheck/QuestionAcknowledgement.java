package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.util.Date;

public class QuestionAcknowledgement  extends Question{
    @Override
    protected void refreshChild(TupleQuestionSection row) {

        if(this.imageUriOne == null || ( this.imageUriOne != null && imageUriOne.isEmpty()) ) this.imageQuestion = null;
    }

    @Override
    public String getTypeString() {
        return "Acknowledgement";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        return true;
    }

    @Override
    public void removeResources() {
        this.imageQuestion = null;
    }
}
