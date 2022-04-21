package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "question_type")
public class TableQuestionType {

    @PrimaryKey
    public  long id;

    @ColumnInfo(name = "label")
    public  String label;

}
