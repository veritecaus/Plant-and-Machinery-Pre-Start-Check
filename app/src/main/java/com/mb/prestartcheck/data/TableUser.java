package com.mb.prestartcheck.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class TableUser {

    @PrimaryKey(autoGenerate = true)
    public  long id;

    @ColumnInfo(name="first_name")
    public  String first_name;

    @ColumnInfo(name="last_name")
    public  String last_name;

    @ColumnInfo(name="pin")
    public  String pin;

    @ColumnInfo(name="enabled")
    public  int enabled;

    @ColumnInfo(name="updated_datetime")
    public long updated_datetime;

    @ColumnInfo(name="created_datetime")
    public long created_datetime;

    @ColumnInfo(name="last_login")
    public long last_login;

    @ColumnInfo(name="role_id")
    public long role_id;

    @ColumnInfo(name="deleted")
    public int deleted;

}
