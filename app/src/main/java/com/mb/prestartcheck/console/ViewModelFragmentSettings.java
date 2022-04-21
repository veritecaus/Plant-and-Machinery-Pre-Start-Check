package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.WorkerEmailBypass;
import com.mb.prestartcheck.WorkerEmailResponse;

public class ViewModelFragmentSettings extends AndroidViewModel {

    public interface OnSettingSavedHandler
    {
        void OnSaved();
    }
    public ViewModelFragmentSettings(@NonNull Application application) {
        super(application);
    }

    public  void updateSettings(String mname,  boolean emailResponses,  boolean emailBypasses,
                                String companyLogo,
                                String machineOperatingHours,
                                Settings.OnSavedHandler onSavedHandler)
    {
        if (!emailResponses) {
            AppLog.getInstance().print("Stopping worker from emailing reponses.");
            WorkerEmailResponse.cancelPeriodic(getApplication().getApplicationContext());
        }
        else
        {
            String freqResponses = App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FREQUENCY);
            AppLog.getInstance().print("Enqueing worker for emailing reponses %s.", freqResponses);

            WorkerEmailResponse.startPeriodic(getApplication().getApplicationContext(), freqResponses);
        }

        if (!emailBypasses) {
            AppLog.getInstance().print("Stopping worker from emailing bypasses.");
            WorkerEmailBypass.cancelPeriodic(getApplication().getApplicationContext());
        }
        else
        {
            String freqResponses = App.getInstance().getSettings().get(Settings.BYPASS_REPORT_RECIPIENT_FREQUENCY);
            AppLog.getInstance().print("Enqueing worker for emailing bypasses %s.", freqResponses);
            WorkerEmailBypass.startPeriodic(getApplication().getApplicationContext(), freqResponses);
        }


        App.getInstance().getSettings().set(Settings.MACHINE_NAME, mname);
        App.getInstance().getSettings().set(Settings.EMAIL_RESPONSES, Boolean.toString(emailResponses));
        App.getInstance().getSettings().set(Settings.EMAIL_BYPASSES, Boolean.toString(emailBypasses));
        App.getInstance().getSettings().set(Settings.COMPANY_LOGO, companyLogo);
        App.getInstance().getSettings().set(Settings.TIMEOUT_MACHINE_OPERATING_HOURS, machineOperatingHours);

        Settings.saveToDatabase(App.getInstance().getSettings(), this.getApplication(), onSavedHandler);

    }

    /**
     * Click handler for the read operting hours button.
     * @param onComplete Display the read value .
     */
    public  void readOpHours(Runnable onComplete)
    {
        ///Off load work the PLC proxy
        ProxyInterlockDevice.readOperatingHours(onComplete);
    }
    /**
     * Click handler for the write operating hours button.
     * @param minutes Value in minutes
     * @param onComplete Notify the user
     */
    public void setOpHours(int minutes, Runnable onComplete)
    {
        ///Off load work the PLC proxy.
        ProxyInterlockDevice.writeOperatingHours(minutes, onComplete);

    }


}
