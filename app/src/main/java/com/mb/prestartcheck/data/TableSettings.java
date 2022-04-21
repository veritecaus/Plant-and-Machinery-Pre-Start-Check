package com.mb.prestartcheck.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "settings")
public class TableSettings {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "created_datetime")
    public Date created_datetime;

    @ColumnInfo(name = "updated_datetime")
    public Date updated_datetime;

    @ColumnInfo(name = "deleted")
    public int deleted;

}
