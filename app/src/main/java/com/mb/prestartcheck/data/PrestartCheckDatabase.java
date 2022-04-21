package com.mb.prestartcheck.data;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(version = 2, entities = {TableUser.class, TableRole.class, TableQuestion.class, TableSection.class, TableQuestionType.class
        , TableSectionQuestion.class, TableResponse.class, TableSession.class, TableSettings.class})
@TypeConverters({Converters.class})
public abstract class PrestartCheckDatabase extends RoomDatabase {

    private static volatile PrestartCheckDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract DaoUser getDaoUser();
    public abstract DaoRole getDaoRole();
    public abstract DaoSection getDaoSection();
    public abstract DaoQuestion getDaoQuestion();
    public abstract DaoSectionQuestion getDaoSectionQuestion();
    public abstract DaoResponse getDaoResponse();
    public abstract DaoSession getDaoSession();
    public abstract DaoSettings getDaoSettings();
    public abstract DaoQuestionType getDaoQuestionType();

    public static PrestartCheckDatabase getDatabase(final Context context) {

        if (instance == null) {
            synchronized (PrestartCheckDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            PrestartCheckDatabase.class, "prestartcheck.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .fallbackToDestructiveMigration()
                            .createFromAsset("preload_v_2.db")
                            .build();
                }
            }
        }
        return instance;
    }

    public  static ExecutorService getDatabaseWriteExecutor()  { return databaseWriteExecutor;}

}
