package com.mb.prestartcheck.data;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface DaoResponse {

    @Insert
    public long insert(TableResponse response);

    @Update
    public void update(TableResponse response);

    @Delete
    public void delete(TableResponse response);

    @Query("SELECT * FROM response WHERE deleted = 0 ORDER BY date, question_sequence")
    List<TableResponse> getAll();

    @Query("SELECT * FROM response WHERE deleted = 0 AND isExported = :exported ORDER BY date, question_sequence")
    List<TableResponse> getAllExported(int exported);

    @Query("SELECT * FROM response WHERE deleted = 0  AND (first_name = :fName OR :fName IS NULL) AND (last_name = :lName  OR :lName IS NULL) " +
            " AND ( created_datetime >= :sDate OR :sDate IS NULL)" +
            " AND ( created_datetime <= :eDate OR :eDate IS NULL)" +
             "  ORDER BY date, question_sequence")
    List<TableResponse> get(@Nullable String fName, @Nullable String lName,@Nullable Date sDate, @Nullable Date eDate);

    @Query("UPDATE response SET isExported = 1 WHERE isExported = 0 AND deleted = 0;")
    public void setExported();


}
