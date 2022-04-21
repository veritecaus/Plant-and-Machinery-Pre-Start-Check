package com.mb.prestartcheck;

import android.content.Context;

public class WorkflowHeartbeat implements Machine.MachineCommsListener  {

    private Machine machine = new Machine();
    private Context context;

    public  void run(Context ctx, String deviceAddr, String finsNodeNumber)
    {
        this.context = ctx;
        this.machine.initComms(this, deviceAddr, finsNodeNumber);
    }


    @Override
    public void onMachineInitComms(Boolean connected) {


    }

}
