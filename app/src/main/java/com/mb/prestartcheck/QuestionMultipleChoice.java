package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.util.Date;

public class QuestionMultipleChoice extends Question {

    private String customField1;

    public String getCustomField1() { return customField1;}
    public  void setCustomField1(String value) { this.customField1 = value;}

    @Override
    protected void refreshChild(TupleQuestionSection row) {
        customField1 = row.custom_field_1;

        if(this.imageUriOne == null || ( this.imageUriOne != null && imageUriOne.isEmpty()) ) this.imageQuestion = null;
    }

    @Override
    public String getTypeString() {
        return "Multi-Choice";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        if (this.expectedAnswer == null ) return true;

        if (this.expectedAnswer.isEmpty())  return true;

        return this.expectedAnswer.compareToIgnoreCase(response.getOperatorResponse()) == 0;
    }

    @Override
    public void removeResources() {
        this.imageQuestion = null;
    }

    public void setImageQuestion(ImageLocal imageQuestion) {
        this.imageQuestion = imageQuestion;
    }

}
