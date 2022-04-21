package com.mb.prestartcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoSection {
    @Query("SELECT * FROM section WHERE deleted = 0 ORDER BY sequence ASC;")
    LiveData<List<TableSection>> getAll();

    @Insert
    void Insert(TableSection s);

    @Update
    void Update(TableSection s);

    @Query("UPDATE section SET deleted = 1 , updated_datetime = :updatedDateTime WHERE id = :id;")
    void delete(long id, long updatedDateTime);


}
