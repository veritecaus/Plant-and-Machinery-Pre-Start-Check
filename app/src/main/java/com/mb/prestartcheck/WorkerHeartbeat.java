package com.mb.prestartcheck;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;



public class WorkerHeartbeat extends Worker {
    public WorkerHeartbeat(@NonNull  Context context, @NonNull  WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    
    @Override
    public Result doWork() {
        try
        {
            AppLog.getInstance().print("Starting WorkerHeartbeat...");

            final String deviceAddr  = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS);
            final  String finsNodeNumber = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS);

            WorkflowHeartbeat workflowHeartbeat = new WorkflowHeartbeat();
            workflowHeartbeat.run(this.getApplicationContext(), deviceAddr, finsNodeNumber);

            return Result.success();
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
        return Result.failure();
    }
}
