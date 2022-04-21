package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.WorkerEmailBypass;
import com.mb.prestartcheck.WorkerEmailResponse;

public class ViewModelFragmentSettingsReportingOptions  extends AndroidViewModel {

   public ViewModelFragmentSettingsReportingOptions(@NonNull Application application) {
        super(application);
   }

   public void updateSettings(String rep1, String rep2, String rep3, String rep4, String f,
                              Settings.OnSavedHandler onSavedHandler)
   {

       String currentFreq =App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FREQUENCY);
       boolean emailResp = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_RESPONSES));

       if (!currentFreq.isEmpty() && currentFreq.compareToIgnoreCase(f) != 0 && emailResp)
       {
           AppLog.getInstance().print("Settings changed for responses reporting frequency. Restarting worker.");
           WorkerEmailResponse.cancelPeriodic(getApplication().getApplicationContext());
           WorkerEmailResponse.startPeriodic(getApplication().getApplicationContext(), f);
       }

       App.getInstance().getSettings().set(Settings.REPORT_RECIPIENT_ONE, rep1);
       App.getInstance().getSettings().set(Settings.REPORT_RECIPIENT_TWO, rep2);
       App.getInstance().getSettings().set(Settings.REPORT_RECIPIENT_THREE, rep3);
       App.getInstance().getSettings().set(Settings.REPORT_RECIPIENT_FOUR, rep4);
       App.getInstance().getSettings().set(Settings.REPORT_RECIPIENT_FREQUENCY, f);

       Settings.saveToDatabase(App.getInstance().getSettings(), getApplication(),  onSavedHandler);


   }
}
