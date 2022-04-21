package com.mb.prestartcheck.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface DaoSettings {

    @Query("SELECT * FROM settings WHERE deleted = 0 ORDER BY `key`")
    LiveData<List<TableSettings>> getAll();

    @Update(entity = TableSettings.class)
    void update(List<TableSettingsUpdate> tableSettingsUpdates);

    @Query("UPDATE settings SET value = :value, updated_datetime= :updated_datetime WHERE `key`=:key")
    void updateValues(String key, String value, Date updated_datetime);

}
