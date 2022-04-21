package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "role")
public class TableRole {
    @PrimaryKey
    public  long id;

    @ColumnInfo(name = "label")
    public  String label;
}
