package com.mb.prestartcheck.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "response")
public class TableResponse {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="machine")
    public String machine;

    @ColumnInfo(name="first_name")
    public String first_name;

    @ColumnInfo(name="last_name")
    public String last_name;

    @ColumnInfo(name="date")
    public long date;

    @ColumnInfo(name="time")
    public long time;

    @ColumnInfo(name="section")
    public long section;

    @ColumnInfo(name="question_sequence")
    public int question_sequence;

    @ColumnInfo(name="question_id")
    public long question_id;

    @ColumnInfo(name="question_title")
    public String question_title;

    @ColumnInfo(name="pos_neg")
    public int pos_neg;

    @ColumnInfo(name="question_text")
    public String question_text;

    @ColumnInfo(name="response_type")
    public String response_type;

    @ColumnInfo(name="expected_response")
    public String expected_response;

    @ColumnInfo(name="operator_response")
    public String operator_response;

    @ColumnInfo(name="machine_unlocked")
    public int  machine_unlocked;

    @ColumnInfo(name="answer_reviewed")
    public int  answer_reviewed;

    @ColumnInfo(name="cleared_by")
    public String  cleared_by;

    @ColumnInfo(name="cleared_at")
    public long  cleared_at;

    @ColumnInfo(name="session_uuid")
    public String  session_uuid;

    @ColumnInfo(name="updated_datetime")
    public long  updated_datetime;

    @ColumnInfo(name="created_datetime")
    public long  created_datetime;

    @ColumnInfo(name="deleted")
    public int  deleted;

    @ColumnInfo(name="isNegative")
    public int  isNegative;

    @ColumnInfo(name="isExported")
    public int  isExported;


}
