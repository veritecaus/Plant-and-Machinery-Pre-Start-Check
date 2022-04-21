package com.mb.prestartcheck.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoQuestionType {

    @Query("SELECT * FROM question_type")
    LiveData<List<TableQuestionType>> getAll();
}
