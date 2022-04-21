package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;
import com.mb.prestartcheck.data.DaoSession;
import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewModelFragmentLogin extends AndroidViewModel  {

    private  User user;

    public ViewModelFragmentLogin(@NonNull Application application,
                                  long userid) {
        super(application);

        this.user  = Operators.getInstance().find(userid);
        if (this.user == null) this.user = Supervisors.getInstance().find(userid);
    }

    public User getUser() {
        return this.user;
    }


    public void login(Runnable oncomplete)
    {

        DaoUser daoUser = PrestartCheckDatabase.getDatabase(this.getApplication()).getDaoUser();
        DaoSession daoSession = PrestartCheckDatabase.getDatabase(this.getApplication()).getDaoSession();

        PrestartCheckDatabase.getDatabaseWriteExecutor().execute(new Runnable() {
            @Override
            public void run() {

                daoUser.login(ViewModelFragmentLogin.this.user.getId(), (new Date()).getTime());

                AppLog.getInstance().reportEvent(ViewModelFragmentLogin.this.user.getFullName() ,(new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())), ActionsEnum.EVENT_SUPERVISOR_LOGIN_ACTION.name(), ResultEnum.RESULT_SUCCESS.name());

                Session.getInstance().login(ViewModelFragmentLogin.this.user);

                long sessionId = daoSession.insert(Session.toTableSession(Session.getInstance()));

                Session.getInstance().setId(sessionId);
                Questioner.getInstance().getQuestionerState().setUser(ViewModelFragmentLogin.this.user);

                oncomplete.run();
            }
        });
    }
}
