package com.mb.prestartcheck;

import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoRole;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TableRole;

import java.util.ArrayList;
import java.util.List;

public class Roles {

    ArrayList<Role> roles = new ArrayList<Role>();
    static Roles instance;

    public Roles() {
    }


    public final List<Role> getList() { return roles;}

    public static Roles getInstance()
    {
        if (instance == null ) instance = new Roles();
        return instance;
    }
    public  Role getAt(int idx)
    {
        return idx >=0  && idx < roles.size() ? roles.get(idx) : null;
    }

    public boolean contains(long id)
    {
        for(Role r : roles)
        {
            if (r.getId() == id) return true;
        }

        return false;
    }

    public Role find(long id)
    {
        for(Role r : roles)
        {
            if (r.getId() == id) return r;
        }

        return null;
    }

    public void remove(Role e)
    {
        roles.remove(e);
    }

    public int getSize() {
        return roles.size();
    }

    public  void clear()
    {
        roles.clear();
    }

    public  void add(Role e)
    {
        roles.add(e);
    }

    public Role find(String label)
    {
        for(Role r : roles) {
            if (r.getLabel().compareTo(label) == 0) return r;
        }
        return null;
    }

    public  void addObserver(AppCompatActivity app)
    {
        DaoRole daoRole = PrestartCheckDatabase.getDatabase(app).getDaoRole();
        if (!daoRole.getAll().hasObservers())
        {
            daoRole.getAll().observe(app, rows->{
                Roles.sync(rows, this);
            });
        }

    }

    public  static void sync(List<TableRole> src, Roles dest)
    {

        for(TableRole row : src)
        {
            Role role ;
            if (!dest.contains(row.id)) {
                role = new Role();
                dest.add(role);
            }
            else
                role = dest.find(row.id);

            role.refresh(row);
        }

        //Remove deleted items
        for(int idx = 0; idx < dest.getSize(); idx++)
        {
            Role r = dest.getAt(idx);
            //Check if the id exists in the src collection.
            boolean keep = false;
            for(TableRole row : src) {
                if (row.id == r.getId()) {
                    keep = true;
                    break;
                }
            }

            if (!keep) dest.remove(r);

        }
    }

}
