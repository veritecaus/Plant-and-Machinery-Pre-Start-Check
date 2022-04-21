package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;

import java.util.ArrayList;
import java.util.List;

public class ViewModelFragmentLoginSupervisor extends AndroidViewModel {

    public ViewModelFragmentLoginSupervisor(Application application) {
        super(application);
    }


    public List<Supervisor> getSupervisors()
    {
        return Supervisors.getInstance().getList(User.pred_user_enabled);
    }
}
