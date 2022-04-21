package com.mb.prestartcheck;


import android.util.Log;

/***********************************************
 * Process wide class that stores the
 * set of questions and sections, user and machine.
 **********************************************/
public class App  implements  Questioner.QuestionerStateListener {
    private static volatile App instance;
    public static final String TAG = "PSCDEBUG";
    private Settings settings = new Settings();
    private ImageLocal imageCompanyLogo;

    public  ImageLocal getImageCompanyLogo() { return this.imageCompanyLogo;}
    public void setImageCompanyLogo(ImageLocal imagelocal) { this.imageCompanyLogo = imagelocal;}

    private App()
    {

    }

    public static App getInstance() {
        if (instance == null)
        {
            instance = new App();
        }

        return instance;
    }


    public Settings getSettings() {
        return settings;
    }

    @Override
    public void onQuestionerBeforeRestart(Question lastQuestion, boolean openInterlock) {
        if (openInterlock) {
            if (lastQuestion  != null && lastQuestion.getAllowMachineOperation()) {
                AppLog.getInstance().print("Questioner restarted questioning. Opening interlock device.");
                ProxyInterlockDevice.openInterlockDevice();
            }
            else
                AppLog.getInstance().print("Questioner restarted questioning but the last question was not operating the machine.");
        }
    }

    @Override
    public void onQuestionerBeforePause(Question lastQuestion) {
        if (lastQuestion != null && lastQuestion.getAllowMachineOperation()) {
            AppLog.getInstance().print("Questioner paused questioning. Opening interlock device.");
            ProxyInterlockDevice.openInterlockDevice();
        }
        else
            AppLog.getInstance().print("Questioner paused questioning but the last question was not operating the machine.");


    }
}
