package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class WorkerEmailResponse extends Worker {

    private static final String TAG = "WorkerEmailReponse";


    public WorkerEmailResponse(@NonNull  Context context, @NonNull  WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {

            AppLog.getInstance().print("Starting WorkerEmailResponse...");


            WorkFlowEmailReponses workFlowEmailReponses = new WorkFlowEmailReponses();

            Data idata = getInputData();
            boolean updateExported = true;

            if (idata != null && idata.hasKeyWithValueOfType("update_exported", Boolean.class))
                updateExported = idata.getBoolean("update_exported", true);

            workFlowEmailReponses.run(getApplicationContext(), updateExported);

            return Result.success();
        }
        catch  (Exception ex)
        {
            Log.e(App.TAG, ex.getMessage());
        }

        return Result.failure();

    }

    public static void startPeriodic(Context context, String frequency)
    {

        Calendar cal = Calendar.getInstance();

        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59);
        Date startTime = cal.getTime();
        Date now = new Date();

        long delay = startTime.getTime() - now.getTime();

        AppLog.getInstance().print("Starting WorkerEmailReponse with initial delay of %d milliseconds.", delay);

        TimeSpan ts = new TimeSpan(frequency);
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(WorkerEmailResponse.class,
                ts.getDays(),  TimeUnit.DAYS)
                .setInitialDelay(delay,TimeUnit.MILLISECONDS)
                .build();

        WorkManager workManager = WorkManager.getInstance(context);

        workManager.enqueueUniquePeriodicWork(TAG ,  ExistingPeriodicWorkPolicy.KEEP, workRequest);

    }

    public static void isExisting(Context context, String frequency)
    {
        WorkManager workManager = WorkManager.getInstance(context);

    }

    public  static void cancelPeriodic(Context context)
    {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(TAG);
    }




}
