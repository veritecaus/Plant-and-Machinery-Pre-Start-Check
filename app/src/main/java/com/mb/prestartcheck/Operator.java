package com.mb.prestartcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.data.TableUser;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Operator extends User {

    public  Operator()
    {

    }

    public  Operator(String fn, String ln, String p) {
        super(fn, ln, p);
    }
    public  Operator(long id, String fn, String ln, String p) {
        super(id, fn, ln, p);
    }

    public  String getFullName() { return String.format("%s %s", this.firstName, this.lastName);}


    public  static Operator createFromTable(TableUser table)
    {
        Operator  op= new Operator(table.id, table.first_name, table.last_name, table.pin );
        op.enabled = table.enabled == 1;
        op.lastLogin = new Date(table.last_login);
        op.setCreatedDateTime( new Date(table.created_datetime));
        op.setUpdatedDatetime( new Date(table.updated_datetime));
        return op;
    }



    public static void sync(Operator op, TableUser row)
    {
        op.id = row.id;
        op.firstName = row.first_name;
        op.lastName = row.last_name;
        op.pin = row.pin;
        op.enabled = row.enabled == 1;
        op.lastLogin = new Date(row.last_login);
        op.createdDateTime = new Date(row.created_datetime);
        op.updatedDatetime = new Date(row.updated_datetime);

    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
          return String.format("%s %s", this.firstName, this.lastName);
    }
}
