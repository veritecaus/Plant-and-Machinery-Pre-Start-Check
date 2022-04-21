package com.mb.prestartcheck.console;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.MonitorInterlockDeviceTaskTimer;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.data.DaoSession;
import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;

import java.util.Date;
import java.util.Timer;

import javax.crypto.Mac;


public class ViewModelFragmentLogout extends AndroidViewModel implements  Session.InterlockDeviceMonitorListener  {



    public  interface  OnLogoutCompletedHandler
    {
        void loggedOut();
    }

    private MonitorInterlockDeviceTaskTimer taskTimer;
    private Timer timer;
    private final String TIMER_TAG = "TIMER_MONITOR_INTERLOCK_DEVICE";

    private  OnLogoutCompletedHandler logoutlistener;

    public ViewModelFragmentLogout(@NonNull  Application application) {
        super(application);
    }

    public ViewModelFragmentLogout(@NonNull  Application application, @NonNull OnLogoutCompletedHandler listener) {
        this(application);
        this.logoutlistener= listener;

    }

    public Questioner getQuestioner () { return Questioner.getInstance();}

    public void restartQuestions()
    {
        Questioner.getInstance().restart(false);
    }

    public void questioningCompleted()
    {

        int hoursTimeout = Integer.parseInt(App.getInstance().getSettings().get(Settings.TIMEOUT_MACHINE_OPERATING_HOURS)) * 60;

        ProxyInterlockDevice.closeInterlockDevice(hoursTimeout);

        Session.getInstance().stopInterlockDeviceMonitoring();
        Session.getInstance().startInterlockDeviceMonitoring(this);


    }

    public  void logout()
    {
        ProxyInterlockDevice.openInterlockDevice();
        //Restart questioning.
        Session.getInstance().logout(getApplication().getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                if (logoutlistener  != null) logoutlistener.loggedOut();
            }
        });

    }


    @Override
    public void interlockDeviceMonitorIsOpen() {
        logout();
    }

}
