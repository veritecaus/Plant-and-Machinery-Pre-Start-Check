package com.mb.prestartcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.data.TableRole;

public class Role {
    private long id;
    private String label;

    public long getId() { return this.id;}
    public void setId(long value) { this.id = value;}

    public String getLabel() { return this.label;}
    public void setLabel(String value) { this.label = value;}

    public Role()
    {

    }

    public Role(long id, String label)
    {
        this.id = id;
        this.label = label;
    }

    public static Role createFromTable(TableRole tbl)
    {
        Role role = new Role(tbl.id, tbl.label);
        return role;

    }

    public  void refresh(TableRole row)
    {
        this.id = row.id ;
        this.label = row.label;

    }

    @NonNull
    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != Role.class) return false;
        return ((Role)obj).getId() == id;
    }
}
