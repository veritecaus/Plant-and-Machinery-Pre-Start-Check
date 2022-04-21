package com.mb.prestartcheck;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoQuestionType;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestionType;

import java.util.ArrayList;
import java.util.List;

public class QuestionTypes {

    private volatile ArrayList<QuestionType> questionTypes = new ArrayList<QuestionType>();
    private static QuestionTypes instance;

    public  QuestionTypes()
    {

    }

    public static QuestionTypes getInstance() {
        if (instance == null) instance = new QuestionTypes();
        return instance;
    }

    public  QuestionType getAt(int idx)
    {
        return idx >=0  && idx < questionTypes.size() ? questionTypes.get(idx) : null;
    }

    public  QuestionType find(long id)
    {
        for(QuestionType q : questionTypes)
            if (q.getId() == id) return q;

        return null;
    }

    public boolean contains(long id)
    {
        for(QuestionType q : questionTypes)
            if (q.getId() == id) return true;

        return false;
    }

    public int getSize() {
        return questionTypes.size();
    }

    public  void clear()
    {
        questionTypes.clear();
    }

    public  void add(QuestionType e)
    {
        questionTypes.add(e);
    }
    public  void remove (QuestionType e)
    {
        questionTypes.remove(e);
    }

    public void addObserver(AppCompatActivity app)
    {
        //Hook observer to the database
        DaoQuestionType daoQuestionType =  PrestartCheckDatabase.getDatabase(app).getDaoQuestionType();
        if (!daoQuestionType.getAll().hasObservers())
        {
            daoQuestionType.getAll().observe(app,  rows->{
                /**********************************************************************
                 *  There was a change in the question table.
                 **********************************************************************/
                QuestionTypes.sync(rows, this);

                Log.i(App.TAG, String.format("QuestionType observer called : QuestionTypes synced:  found %d.", questionTypes.size()));
            });

        }

    }


    public  static void sync(List<TableQuestionType> src, QuestionTypes dest)
    {

        for(TableQuestionType row : src)
        {
            QuestionType questionType = null ;
            if (!dest.contains(row.id)) {
                questionType = new QuestionType(row.id, row.label);
                dest.add(questionType);
            }
            else
                questionType = dest.find(row.id);

            questionType.refresh(row);
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            QuestionType q = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TableQuestionType r : src) {
                if (r.id == q.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep) dest.remove(q);

        }


    }

    public  Iterable<QuestionType> getIterable() { return this.questionTypes;}

}
