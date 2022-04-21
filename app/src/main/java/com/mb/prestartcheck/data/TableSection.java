package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "section")

public class TableSection {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "show_cover_page")
    public int  show_cover_page;

    @ColumnInfo(name = "sequence")
    public int  sequence;

    @ColumnInfo(name = "random_questions")
    public int  random_questions;

    @ColumnInfo(name = "enabled")
    public int  enabled;

    @ColumnInfo(name = "deleted")
    public int  deleted;

    @ColumnInfo(name="updated_datetime")
    public long updated_datetime;

    @ColumnInfo(name="created_datetime")
    public long created_datetime;


}
