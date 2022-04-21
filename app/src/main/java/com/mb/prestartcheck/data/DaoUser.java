package com.mb.prestartcheck.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Update;


@Dao
public interface DaoUser {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertUser(TableUser user);

    @Query("SELECT * FROM user WHERE deleted = 0 AND role_id = 1 ORDER BY id")
    LiveData<List<TableUser>> getAllOperators();

    @Query("SELECT * FROM user WHERE deleted = 0 AND role_id = 2 ORDER BY id")
    LiveData<List<TableUser>> getAllSupervisors();

    @Update
    public  void updateUser(TableUser user);

    @Query("UPDATE user SET last_login = :datetime , updated_datetime = :datetime WHERE id = :id")
    public void login(long id, long datetime);

}
