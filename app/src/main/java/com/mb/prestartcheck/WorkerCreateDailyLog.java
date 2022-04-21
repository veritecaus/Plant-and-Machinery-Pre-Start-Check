package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkerCreateDailyLog extends Worker {

    private static final String TAG = "WorkerCreateDailyLog";

    public WorkerCreateDailyLog(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppLog.getInstance().print("Starting doWork WorkerCreateDailyLog ");
        try {
            return Result.success();
        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }
        return Result.success();
    }

    public static void startPeriodic(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59);
        Date startTime = cal.getTime();
        Date now = new Date();
        long delay = startTime.getTime() - now.getTime();
        AppLog.getInstance().print("Starting WorkerCreateDailyLog with initial delay of %d milliseconds.", delay);

        TimeSpan timeSpan = new TimeSpan("daily");
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(WorkerCreateDailyLog.class,
                timeSpan.getDays(), TimeUnit.DAYS)
                .build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest);
/*
 ** For Testing with short interval

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(WorkerCreateDailyLog.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG,
                ExistingPeriodicWorkPolicy.KEEP, workRequest);

        */
    }
}