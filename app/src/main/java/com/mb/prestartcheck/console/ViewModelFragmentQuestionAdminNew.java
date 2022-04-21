package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.mb.prestartcheck.QuestionType;
import com.mb.prestartcheck.QuestionTypes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewModelFragmentQuestionAdminNew extends AndroidViewModel {

    private ArrayList<QuestionType> list = new ArrayList<QuestionType>();

    public ViewModelFragmentQuestionAdminNew(@NonNull @NotNull Application application) {
        super(application);
        for(QuestionType item :  QuestionTypes.getInstance().getIterable())
            list.add(item);

    }

    public List<QuestionType> getList()
    {
        return list;
    }

}