package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="section_question")
public class TableSectionQuestion {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="section_id")
    public long section_id;

    @ColumnInfo(name="question_id")
    public long question_id;

    @ColumnInfo(name="updated_datetime")
    public long updated_datetime;

    @ColumnInfo(name="created_datetime")
    public long created_datetime;

    @ColumnInfo(name="enabled")
    public int enabled;

    @ColumnInfo(name="deleted")
    public int deleted;

}
