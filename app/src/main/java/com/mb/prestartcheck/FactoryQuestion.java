package com.mb.prestartcheck;

public class FactoryQuestion {

    public static Question  create(int typeid)
    {
        if (typeid == 1)
            return new QuestionYesNo();
        else if (typeid == 2)
            return new QuestionMultipleChoice();
        else if (typeid == 3)
            return new QuestionOptions();
        else if (typeid == 4)
            return new QuestionAcknowledgement();

        return null;
    }
}
