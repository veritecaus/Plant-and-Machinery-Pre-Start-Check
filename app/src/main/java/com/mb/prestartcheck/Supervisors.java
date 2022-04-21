package com.mb.prestartcheck;

import android.content.Context;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableUser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Supervisors  implements Iterable<Supervisor> {


    public interface onSyncListener
    {
        void syncCompleted();
    }

    private ArrayList<Supervisor> supervisors = new ArrayList<Supervisor>();
    private Hashtable<Context, Supervisors.onSyncListener> listeners = new Hashtable<Context, Supervisors.onSyncListener>();
    static Supervisors instance;

    public  Supervisors()
    {

    }

    public static Supervisors getInstance() {
        if (instance == null) instance = new Supervisors();
        return instance;
    }

    public int getSize() { return this.supervisors.size();}

    public boolean contains(long id)
    {
        for(Supervisor s : this.supervisors)
            if (s.getId() == id) return true;
        return false;
    }

    public Supervisor getAt(int index)
    {
        return index >=0 && index < this.supervisors.size() ? this.supervisors.get(index) :null;
    }

    public void add(Supervisor op) {
        this.supervisors.add(op);
    }
    public void clear()
    {
        this.supervisors.clear();
    }

    public  void remove(Supervisor e) { this.supervisors.remove(e);}

    public Supervisor find(long id)
    {
        for(Supervisor s : this.supervisors)
            if (s.getId() == id ) return s;
        return null;
    }

    public Supervisor last()
    {
        return supervisors.size() > 0 ? supervisors.get(supervisors.size()-1) : null;
    }

    public void addListener(Context c,  Supervisors.onSyncListener e)
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
        if (!daoUser.getAllSupervisors().hasObservers())
        {
            daoUser.getAllSupervisors().observe(app, rows->{

                Supervisors.sync(rows, this);

                Log.i(App.TAG, String.format("synced supervisors, and found %d.", supervisors.size()));
                for(Enumeration<Supervisors.onSyncListener> e = listeners.elements(); e.hasMoreElements();)
                    e.nextElement().syncCompleted();

            });
        }
    }

    public  static void sync(List<TableUser> src, Supervisors dest)
    {
        for(TableUser row : src)
        {
            if (!dest.contains(row.id))
            {
                Supervisor su = Supervisor.createFromTable(row);
                dest.add(su);
            }
            else
            {
                Supervisor sdest = dest.find(row.id);
                Supervisor.sync(sdest, row);
            }
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            Supervisor su = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TableUser u : src) {
                if (u.id == su.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep) dest.remove(su);

        }
    }

    public  List<Supervisor> getList()
    {
        return supervisors;
    }

    public  List<Supervisor> getList(Predicate<User> predicate)
    {
        ArrayList<Supervisor> list = new ArrayList<>();
        for(Supervisor s : this.supervisors) {
            if (predicate.test(s))
                list.add(s);
        }

        return list;
    }

    @NonNull
    @Override
    public Iterator<Supervisor> iterator() {
        return this.supervisors.iterator();
    }

}
