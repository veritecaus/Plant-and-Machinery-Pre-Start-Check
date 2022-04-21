package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.jar.Attributes;

@Entity(tableName = "session")
public class TableSession {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "operator_id")
    public long operator_id ;

    @ColumnInfo(name = "uuid")
    public String uuid;

    @ColumnInfo(name = "login_date_time")
    public long login_date_time;

    @ColumnInfo(name = "logout_date_time")
    public long logout_date_time;

    @ColumnInfo(name = "deleted")
    public int  deleted;

    @ColumnInfo(name="updated_datetime")
    public long updated_datetime;

    @ColumnInfo(name="created_datetime")
    public long created_datetime;

}
