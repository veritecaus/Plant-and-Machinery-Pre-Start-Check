package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;

public class RowSelectSectionQuestion {

    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "section_id")
    public long section_id;

    @ColumnInfo(name = "question_id")
    public long question_id;

}
