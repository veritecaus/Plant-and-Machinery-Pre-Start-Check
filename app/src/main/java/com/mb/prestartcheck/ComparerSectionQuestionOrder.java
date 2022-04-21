package com.mb.prestartcheck;

import java.util.Comparator;

public class ComparerSectionQuestionOrder implements Comparator<Section> {
    @Override
    public int compare(Section o1, Section o2) {
        return o1.getDisplaySequence() - o2.getDisplaySequence();
    }
}
