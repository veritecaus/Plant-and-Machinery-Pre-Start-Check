package com.mb.prestartcheck;

import java.util.Comparator;

public class ComparerQuestioningOrder implements Comparator<Question> {
    @Override
    public int compare(Question o1, Question o2) {
        return o1.getQuestioningOrder() - o2.getQuestioningOrder();
    }


}
