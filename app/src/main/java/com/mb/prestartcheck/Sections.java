package com.mb.prestartcheck;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoSection;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Sections implements java.util.Comparator<Section> {

    volatile ArrayList<Section> sections = new ArrayList<Section>();
    //Singleton
    static  Sections instance;

    public interface onSyncListener
    {
        void syncCompleted();
    }

    private Hashtable<Context, Sections.onSyncListener> listeners = new Hashtable<Context, Sections.onSyncListener>();

    public  Sections()
    {

    }

    public  Sections(Sections src)
    {
        sections.clear();
        sections.addAll(src.sections);
    }

    public  Section getAt(int idx)
    {
        return idx >=0  && idx < sections.size() ? sections.get(idx) : null;
    }

    public  Section find(long id)
    {
        for(Section s : sections)
        {
            if (s.getId() == id) return s;
        }

        return null;
    }


    @Nullable
    public  Section first(Predicate<Section> predicate)
    {
        //Get the lowest display order setion.
        int minDisplayIdx = Integer.MAX_VALUE;
        Section firstSection = null;
        for(Section section : this.sections)
        {
            if (predicate.test(section) && section.getDisplaySequence() < minDisplayIdx )
            {
                minDisplayIdx = section.getDisplaySequence();
                firstSection = section;
            }
        }


        return firstSection;

    }

    @Nullable
    public Section find(BiPredicate<Section , Integer> biPredicate, Integer sequence)
    {
        //sort in ascending order
       // if (sort) this.sections.sort(this);
        for(Section section : this.sections)
            if (biPredicate.test(section, sequence)) return section;

        return null;
    }

    public boolean contains(long id)
    {
        for(Section s : sections)
        {
            if (s.getId() == id) return true;
        }

        return false;
    }

    public int getSize() {
        return sections.size();
    }

    public int count(Predicate<Section> predicate)
    {
        int cnt = 0 ;
        for(Section section : sections)
            cnt += predicate.test(section) ? 1: 0;

        return cnt;
    }
    public  void clear()
    {
        sections.clear();
    }

    public  void add(Section e)
    {
        sections.add(e);
    }

    public void remove(Section e) { sections.remove(e);}

    public static Sections getInstance() {
        if (instance == null) instance = new Sections();
        return instance;
    }

    public  static void sync(List<TableSection> src, Sections dest)
    {
        for(TableSection row : src)
        {
            if (!dest.contains(row.id))
                dest.add(Section.createFromTableRow(row));
            else
            {
                Section sdest = dest.find(row.id);
                Section.sync(sdest, row);
            }
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            Section s = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TableSection r : src) {
                if (r.id == s.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep) dest.remove(s);

        }

        dest.sections.sort(dest);

    }

    public void addObserver(AppCompatActivity app)
    {
        //Hook observer to the database
        DaoSection daoSection =  PrestartCheckDatabase.getDatabase(app).getDaoSection();
        if (!daoSection.getAll().hasObservers())
        {
            daoSection.getAll().observe(app,  rows->{

                /**********************************************************************
                 *  There was a change in the sections table.
                 **********************************************************************/
                Sections.sync(rows, this);

                Log.i(App.TAG, String.format("Observer Sections called : sections synced:  found %d.", sections.size()));

                for(Enumeration<Sections.onSyncListener> e = listeners.elements(); e.hasMoreElements();)
                    e.nextElement().syncCompleted();

                //Load questions; done here to ensure sections was loaded first.
                Questions.getInstance().addObserver(app);
            });

        }

    }

    @Override
    public int compare(Section o1, Section o2) {
        return o1.getSequence() - o2.getSequence();
    }

    public  static Section findBySequence(Sections e, int seq)
    {
        for(Section s : e.sections)
        {
            if (s.getSequence() == seq) return s;
        }

        return null;
    }

    public int nextSequence()
    {
        int maxseq = -1;
        for(Section s : this.sections)
        {
            maxseq = s.getSequence() > maxseq ? s.getSequence() : maxseq;
        }

        return ++maxseq;
    }

    public void addListener(Context c,  Sections.onSyncListener e)
    {
        if (!listeners.contains(c))
            listeners.put(c, e);
    }

    public  void removeListener(Context c)
    {
        listeners.remove(c);
    }


    public int nextSequence(Predicate<Section> predicate,  int prevSequence)
    {
        //if (sort) this.sections.sort(this);

        for(Section section : this.sections)
        {
            if ( predicate.test(section) && section.getDisplaySequence() > prevSequence)
                return section.getDisplaySequence();
        }


        return prevSequence + 1;
    }

    public int getMaxSequence(Predicate<Section> predicate)
    {
        int max = Integer.MIN_VALUE;
        for(Section section : this.sections)
        {
            max =  predicate.test(section) && section.getDisplaySequence() > max ? section.getDisplaySequence() : max;
        }

        return  max;

    }

    public  void assignDisplayOrder(Predicate<Section> predicate)
    {

        int displayIndex = 0;
        for(Section section : this.sections)
        {
            if (predicate.test(section))
            {
                section.setDisplaySequence(displayIndex);
                displayIndex++;
            }
            else
                section.setDisplaySequence(Integer.MAX_VALUE);
        }
    }
}
