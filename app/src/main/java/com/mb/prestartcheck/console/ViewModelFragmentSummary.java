package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.ReportLine;
import com.mb.prestartcheck.Reporter;
import com.mb.prestartcheck.Session;

import java.util.List;

public class ViewModelFragmentSummary extends ViewModelFragmentLogout {
    private OnLogoutCompletedHandler logoutlistener;

    public ViewModelFragmentSummary(@NonNull Application application, @NonNull OnLogoutCompletedHandler listener) {
        super(application);
        this.logoutlistener = listener;
    }


    public void logout() {
        AppLog.getInstance().print("In reviewing the operator's responses , and after consideration , the supervisor has decided to logged the operator out.");
        Session.getInstance().stopInterlockDeviceMonitoring();
        questioningCompleted();
        ProxyInterlockDevice.openInterlockDevice();

        Session.getInstance().logout(getApplication().getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                if (logoutlistener != null) logoutlistener.loggedOut();
            }
        });
    }

    public List<ReportLine> getSummary() {
        Reporter reporter = new Reporter(Machine.getInstance(),
                Questioner.getInstance().getQuestionerState().getUser());
        //Get
        return reporter.publishQuestioner();
    }

}