package com.mb.prestartcheck.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DaoSession {
    @Insert
    public long insert(TableSession tableSession);

    @Query("UPDATE session SET logout_date_time = :now where  id = :id;")
    public void logout(long now, long id);
}
