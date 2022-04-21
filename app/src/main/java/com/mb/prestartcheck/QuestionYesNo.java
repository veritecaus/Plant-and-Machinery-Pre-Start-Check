package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.util.Date;

public class QuestionYesNo  extends Question {

    @Override
    protected void refreshChild(TupleQuestionSection row) {
        if(this.imageUriOne == null || ( this.imageUriOne != null && imageUriOne.isEmpty()) ) this.imageQuestion = null;

        this.altComment = row.custom_field_2;
    }

    @Override
    public String getTypeString() {
        return "Yes/No";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        if (response.getIsNegative())
        {
            if (this.expectedAnswerNeg == null ) return true;

            if (this.expectedAnswerNeg.isEmpty())  return true;

            return this.expectedAnswerNeg.compareToIgnoreCase(response.getOperatorResponse()) == 0;
        }
        else
        {
            if (this.expectedAnswer == null ) return true;

            if (this.expectedAnswer.isEmpty())  return true;

            return this.expectedAnswer.compareToIgnoreCase(response.getOperatorResponse()) == 0;

        }

    }

    @Override
    public void removeResources() {
        this.imageQuestion = null;
    }

    public void setImageQuestion(ImageLocal imageQuestion) {
        this.imageQuestion = imageQuestion;
    }
}
