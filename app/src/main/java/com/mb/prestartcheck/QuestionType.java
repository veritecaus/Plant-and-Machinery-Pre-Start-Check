package com.mb.prestartcheck;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.data.TableQuestionType;

public class QuestionType {
    private long id;
    private String label;

    public String getLabel() { return label;}
    public void setLabel(String l) { this.label = l;}

    public long getId() { return this.id;}
    public void setId(long id) { this.id = id;}

    public QuestionType(long id, String label)
    {
        this.id = id;
        this.label = label;
    }

    public void refresh(TableQuestionType row)
    {
        this.label = row.label;
    }

    @NonNull
    @Override
    public String toString() {
        return this.label;
    }
}
