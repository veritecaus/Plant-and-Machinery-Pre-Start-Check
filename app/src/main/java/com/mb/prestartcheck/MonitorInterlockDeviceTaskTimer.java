package com.mb.prestartcheck;

import android.util.Log;

import java.util.TimerTask;

public class MonitorInterlockDeviceTaskTimer  extends TimerTask {

    public interface  MonitorInterlockDeviceListener
    {
        void monitorInterlockDeviceOpened();
    }

    private MonitorInterlockDeviceTaskTimer.MonitorInterlockDeviceListener listener;

    public  MonitorInterlockDeviceTaskTimer(MonitorInterlockDeviceTaskTimer.MonitorInterlockDeviceListener e)
    {
        listener = e;
    }


    @Override
    public void run() {
        try
        {
            Log.i(App.TAG, "Checking to see if the interlock  device is open ...");

            if (Machine.getInstance().hasComms()) {
                Machine.getInstance().isClosed(new Machine.MachineInterlockStateDeviceListener() {
                    @Override
                    public void onMachineInterlockDeviceStateRead(boolean isClosed) {

                        if (!isClosed && listener != null) listener.monitorInterlockDeviceOpened();
                    }
                });
            }
            else {
                AppLog.getInstance().eventPLCTCPIPLost();
                //For the scenario where power is lost to the machine ( and interlock device),
                //the  interlock is considered opened because it is in that state after power
                //is reconnected. So, signal to listeners that the interlock device is opened.

                if (listener != null) listener.monitorInterlockDeviceOpened();

            }

        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


}
