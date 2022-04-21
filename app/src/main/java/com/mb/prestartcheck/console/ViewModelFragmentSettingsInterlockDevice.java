package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.fins.FINSSession;

import org.jetbrains.annotations.NotNull;

public class ViewModelFragmentSettingsInterlockDevice extends AndroidViewModel {

    public ViewModelFragmentSettingsInterlockDevice(@NonNull  Application application) {
        super(application);
    }

    /**
     * Save the changes to settings and add log entries.
     * @param ip IP address of the interlock device
     * @param finsNodeNo  The FINS client node number assigned to the controlling connection.
     * @param alfinsNodeNo   The FINS client node number assigned to the connection that reads bypasses.
     * @param onSavedHandler Called after settings was updated.
     */
    public void update(String ip, String finsNodeNo,  String alfinsNodeNo,Settings.OnSavedHandler onSavedHandler)
    {
        String addr = ip;
        int finNode = Integer.parseInt(finsNodeNo);
        //Current values.
        String oldIP = Machine.getInstance().getCurrentInterlockDeviceAddress();
        int oldFinsNodeNo = Machine.getInstance().getCurrentFINNodeNumber();

        Settings settings = App.getInstance().getSettings();
        String oldAltFinsNodeNo = settings.get(Settings.INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS);

        //Reconnect to the intelock device if the IP address or node number changed.
        boolean reconnect = oldFinsNodeNo != finNode ||  oldIP.compareToIgnoreCase(addr) != 0;

        if (reconnect) {
            AppLog.getInstance().print("Interlock device settings changed. Reconnecting.");
            ProxyInterlockDevice.connectAndInitializeInterlockDevice(addr, Integer.toString(finNode));
        }

        //If a setting was changes, add a log entry.
        if ( oldIP.compareToIgnoreCase(ip) != 0) {
            AppLog.getInstance().eventInterlockDeviceSettingChanged("Interlock device IP address",  ip,  oldIP);
        }

        if (finNode != oldFinsNodeNo) {
            AppLog.getInstance().eventInterlockDeviceSettingChanged("FINS client node",  finsNodeNo,  Integer.toString(oldFinsNodeNo));
        }

        if (alfinsNodeNo.compareToIgnoreCase(oldAltFinsNodeNo) != 0) {
            AppLog.getInstance().eventInterlockDeviceSettingChanged("Alternate FINS client node",  alfinsNodeNo, oldAltFinsNodeNo);
        }

        settings.set(Settings.INTERLOCK_DEVICE_ADDRESS, ip);
        settings.set(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS, finsNodeNo);
        settings.set(Settings.INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS, alfinsNodeNo);

        Settings.saveToDatabase(settings,
                                this.getApplication(),
                                onSavedHandler);
    }
    
    public  void testSettings(@NonNull String deviceAddr, int finsNodeNo, @NonNull ProxyInterlockDevice.ProxyInterlockDeviceListener listener)
    {

        //close the connection
        ProxyInterlockDevice.connect(deviceAddr, Integer.toString(finsNodeNo), listener);

    }


}
