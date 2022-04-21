package com.mb.prestartcheck.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class TableSettingsUpdate {
    @ColumnInfo(name="key")
    public String key;

    @ColumnInfo(name="value")
    public String value;

    @ColumnInfo(name="updated_datetime")
    public Date updated_datetime;

}
