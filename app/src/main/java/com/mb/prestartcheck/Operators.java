package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableSection;
import com.mb.prestartcheck.data.TableUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class Operators implements Iterable<Operator> {


    public interface onSyncListener
    {
        void syncCompleted();
    }

    private ArrayList<Operator> operators = new ArrayList<Operator>();
    private Hashtable<Context, onSyncListener> listeners = new Hashtable<Context, onSyncListener>();
    static Operators instance;

    public  Operators()
    {

    }

    public static Operators getInstance() {
        if (instance == null) instance = new Operators();
        return instance;
    }

    public int getSize() { return this.operators.size();}

    public boolean contains(long id)
    {
        for(Operator o : operators)
            if (o.getId() == id) return true;
        return false;
    }

    public Operator getAt(int index)
    {
        return index >=0 && index < operators.size() ? operators.get(index) :null;
    }

    public void add(Operator op) {
        this.operators.add(op);
    }
    public void clear()
    {
        this.operators.clear();
    }

    public  void remove(Operator e) { this.operators.remove(e);}

    public Operator find(long id)
    {
        for(Operator o : this.operators)
            if (o.getId() == id ) return o;
        return null;
    }

    public Operator last()
    {
        return operators.size() > 0 ? operators.get(operators.size()-1) : null;
    }

    public void addListener(Context c,  onSyncListener e)
    {
        if (!listeners.contains(c))
            listeners.put(c, e);
    }

    public  void removeListener(Context c)
    {
        listeners.remove(c);
    }

    public  void addObserver(AppCompatActivity app)
    {
        DaoUser daoUser = PrestartCheckDatabase.getDatabase(app).getDaoUser();
        if (!daoUser.getAllOperators().hasObservers())
        {
            daoUser.getAllOperators().observe(app, rows->{

                Operators.sync(rows, this);

                Log.i(App.TAG, String.format("synced operators, and found %d.", operators.size()));
                for(Enumeration<onSyncListener> e = listeners.elements(); e.hasMoreElements();)
                    e.nextElement().syncCompleted();

            });
        }
    }

    public  static void sync(List<TableUser> src, Operators dest)
    {
        for(TableUser row : src)
        {
            if (!dest.contains(row.id))
            {
                Operator op = Operator.createFromTable(row);
                dest.add(op);
            }
            else
            {
                Operator sdest = dest.find(row.id);
                Operator.sync(sdest, row);
            }
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            Operator op = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TableUser u : src) {
                if (u.id == op.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep) dest.remove(op);

        }
    }

    //TODO: Remove this.
    public Iterable<Operator> getIterable() { return this.operators;}

    @NonNull
    @Override
    public Iterator<Operator> iterator() {
        return this.operators.iterator();
    }

}
