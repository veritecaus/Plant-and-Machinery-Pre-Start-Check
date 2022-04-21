package com.mb.prestartcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoRole {

    @Query("SELECT * FROM role")
    LiveData<List<TableRole>> getAll();

}
