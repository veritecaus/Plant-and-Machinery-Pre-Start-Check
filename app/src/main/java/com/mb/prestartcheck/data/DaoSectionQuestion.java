package com.mb.prestartcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoSectionQuestion {

    @Query("SELECT id, section_id, question_id FROM section_question where deleted = 0")
    LiveData<List<RowSelectSectionQuestion>> getAll();

    @Insert
    public void insert(TableSectionQuestion row);

    @Query("UPDATE section_question SET deleted = 1, updated_datetime = :updated_datetime WHERE section_id = :section")
    public void delete (long section, long updated_datetime);

    @Query("UPDATE section_question SET deleted = 1, updated_datetime = :updated_datetime WHERE question_id = :question")
    public void deleteQuestion (long question, long updated_datetime);

}
