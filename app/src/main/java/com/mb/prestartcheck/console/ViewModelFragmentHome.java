package com.mb.prestartcheck.console;

import android.app.Application;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Sections;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.Supervisors;

public class ViewModelFragmentHome extends ViewModelQuestionNavigator  {


    public ViewModelFragmentHome(Application app)
    {
        super(app, null, null);
    }

    public  Operators getOperators() {
        return Operators.getInstance();
    }

    public Sections getSections()
    {
        return Sections.getInstance();
    }

    public Questioner getQuestioner()
    {
        return Questioner.getInstance();
    }

    public Supervisors getSuperVisors()
    {
        return Supervisors.getInstance();
    }

    public void checkInterlockDeviceIsOpen()
    {
        ProxyInterlockDevice.openInterlockDevice();
    }


}
