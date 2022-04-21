package com.mb.prestartcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoQuestion {

    @Query("SELECT question.*, section_question.section_id FROM question, section_question WHERE question.id = section_question.question_id" +
            " AND question.deleted = 0 ORDER BY question.sequence ASC")
    LiveData<List<TupleQuestionSection>> getAll();

    @Update
    void update(TableQuestion tableQuestion);

    @Insert
    long insert(TableQuestion tableQuestion);

    @Query("UPDATE question SET deleted = 1 , updated_datetime = :updatedDateTime WHERE id = :qid")
    void delete(long qid, long updatedDateTime );

    @Query("UPDATE Question SET deleted = 1, updated_datetime = :updatedDateTime   WHERE id in (:ids)")
    void deleteIds(List<Long> ids, long updatedDateTime);

    @Query("UPDATE Question SET image_uri_one = :uri, updated_datetime = :updatedDateTime   WHERE id = :id")
    void updateImageURIOne(long id, String uri, long updatedDateTime);

    @Query("UPDATE Question SET image_uri_two = :uri, updated_datetime = :updatedDateTime   WHERE id = :id")
    void updateImageURITwo(long id, String uri, long updatedDateTime);

}
