package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableSettings;

import java.util.ArrayList;

public class ViewModelFragmentSettingsEmailServer extends AndroidViewModel {
    public ViewModelFragmentSettingsEmailServer(@NonNull Application application) {
        super(application);
    }



    public void update(String toAddr, String fromAddr,  String smtpServer,  int port,  String securityType,  boolean requiresSignIn,
                       String userName, String password,   Settings.OnSavedHandler onSavedHandler)
    {

        Settings settings = App.getInstance().getSettings();

        settings.set(Settings.EMAIl_TO, toAddr);
        settings.set(Settings.EMAIl_FROM, fromAddr);
        settings.set(Settings.EMAIl_SMTP, smtpServer);
        settings.set(Settings.EMAIl_SMTP_PORT, Integer.toString(port));
        settings.set(Settings.EMAIL_SECURITY_TYPE, securityType);
        settings.set(Settings.EMAIl_REQUIRES_SIGNIN, Boolean.toString(requiresSignIn));
        settings.set(Settings.EMAIL_USER_NAME, userName);
        settings.set(Settings.EMAIL_USER_PASSWORD, password);

        Settings.saveToDatabase(settings, getApplication(), onSavedHandler);


    }

}
