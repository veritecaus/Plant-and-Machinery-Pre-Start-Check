package com.mb.prestartcheck;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class WorkerEmailBypass extends Worker {

    private static final String TAG = "WorkerEmailBypass";

    public WorkerEmailBypass(@NonNull Context context, @NonNull  WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try
        {
            DateFormaterLog dateFormaterLog = new DateFormaterLog();

            AppLog.getInstance().print("Starting WorkerEmailBypass at %s.", dateFormaterLog.format(new Date()));

            final String deviceAddr  = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS);
            final  String finsNodeNumber = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS);

            AppLog.getInstance().print("WorkerEmailBypass is connecting to %s, using node %s.",deviceAddr,finsNodeNumber  );

            WorkFlowCheckBypassEntries workFlowCheckBypassEntries = new WorkFlowCheckBypassEntries();
            workFlowCheckBypassEntries.run(this.getApplicationContext(), deviceAddr, finsNodeNumber);

             return Result.success();
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
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

        AppLog.getInstance().print("Starting WorkerEmailBypass with initial delay of %d milliseconds.", delay);

        TimeSpan ts = new TimeSpan(frequency);
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(WorkerEmailBypass.class,
                ts.getDays(),  TimeUnit.DAYS).build();

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(TAG,  ExistingPeriodicWorkPolicy.KEEP, workRequest);

    }

    public  static void cancelPeriodic(Context context)
    {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(TAG);
    }

}
