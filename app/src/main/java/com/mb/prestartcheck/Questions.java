package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.DaoSection;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TableSection;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Questions {
    volatile ArrayList<Question> questions = new ArrayList<Question>();
    static Questions instance;


    public interface onSyncListener
    {
        void syncCompleted();
    }

    private Hashtable<Context, Questions.onSyncListener> listeners = new Hashtable<Context, Questions.onSyncListener>();

    public  Questions()
    {

    }

    public static Questions getInstance() {
        if (instance == null) instance = new Questions();
        return instance;
    }

    public void addListener(Context c,  Questions.onSyncListener e)
    {
        if (!listeners.contains(c))
            listeners.put(c, e);
    }

    public  void removeListener(Context c)
    {
        listeners.remove(c);
    }

    public  Question getAt(int idx)
    {
        return idx >=0  && idx < questions.size() ? questions.get(idx) : null;
    }

    public  Question find(long id)
    {
        for(Question q : questions)
            if (q.getId() == id) return q;

        return null;
    }

    public boolean contains(long id)
    {
        for(Question q : questions)
            if (q.getId() == id) return true;

        return false;
    }

    public int getSize() {
        return questions.size();
    }

    public  void clear()
    {
        questions.clear();
    }

    public  void add(Question e)
    {
        questions.add(e);
    }
    public  void remove (Question e)
    {
        questions.remove(e);
    }

    public void addObserver(AppCompatActivity app)
    {
        //Hook observer to the database
        DaoQuestion daoQuestion =  PrestartCheckDatabase.getDatabase(app).getDaoQuestion();
        if (!daoQuestion.getAll().hasObservers())
        {
            daoQuestion.getAll().observe(app,  rows->{
                /**********************************************************************
                 *  There was a change in the question table.
                 **********************************************************************/
                Questions.sync(rows, this);

                for(Enumeration<Questions.onSyncListener> e = listeners.elements(); e.hasMoreElements();)
                    e.nextElement().syncCompleted();

                Log.i(App.TAG, String.format("Question observer called : Questions synced:  found %d.", questions.size()));
            });

        }

    }

    public int getNextSquence(long sectionId)
    {
        int nextseq= Integer.MIN_VALUE;
        for (Question q : this.questions) {
            if (q.getParent().getId() == sectionId)
                nextseq = q.getSequence() > nextseq ? q.getSequence() : nextseq;
        }

        return nextseq == Integer.MIN_VALUE ? 1 : nextseq + 1;
    }


    public  static void sync(List<TupleQuestionSection> src, Questions dest)
    {

        for(TupleQuestionSection row : src)
        {
            Question question ;
            if (!dest.contains(row.id)) {
                question = FactoryQuestion.create(row.type_id);
                dest.add(question);
            }
            else
                question = dest.find(row.id);

            question.refresh(row);
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            Question q = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TupleQuestionSection r : src) {
                if (r.id == q.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep)
            {
                dest.remove(q);
                //Unlink your self;
                if (q.getParent() != null)
                {
                    q.getParent().remove(q);
                }
            }

        }

        //Relationships.
        for(TupleQuestionSection row : src)
        {
            long sectionId = row.section_id;

            Section section = Sections.getInstance().find(sectionId);
            if (section != null )
            {
                Question question = dest.find(row.id);

                if (Section.addQuestion(section, question))
                    Log.i(App.TAG, String.format("Adding %s to section %s. ", question.title, section.getTitle()));
            }

         }

    }

}
