package com.mb.prestartcheck;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.data.TableUser;

import java.util.Date;

public class Supervisor extends User {

    public  Supervisor(long id, String fn, String ln, String p) {
        super(id, fn, ln, p);
    }

    public  Supervisor(String fn, String ln, String p) {
        super(fn, ln, p);
    }

    public  Supervisor()
    {

    }

    @NonNull
    @Override
    public String toString() {
        return super.getFullName();
    }


    public  static Supervisor createFromTable(TableUser table)
    {
        Supervisor  su = new Supervisor(table.id, table.first_name, table.last_name, table.pin );
        su.enabled = table.enabled == 1;
        su.lastLogin = new Date(table.last_login);
        su.setCreatedDateTime( new Date(table.created_datetime));
        su.setUpdatedDatetime( new Date(table.updated_datetime));
        return su;
    }



    public static void sync(Supervisor su, TableUser row)
    {
        su.id = row.id;
        su.firstName = row.first_name;
        su.lastName = row.last_name;
        su.pin = row.pin;
        su.enabled = row.enabled == 1;
        su.lastLogin = new Date(row.last_login);
        su.createdDateTime = new Date(row.created_datetime);
        su.updatedDatetime = new Date(row.updated_datetime);

    }

}
