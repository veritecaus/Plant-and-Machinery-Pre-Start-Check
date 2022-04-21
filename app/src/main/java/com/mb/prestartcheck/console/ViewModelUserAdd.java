package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.Role;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableUser;
import com.mb.prestartcheck.data.DaoUser;

import java.util.Date;

public class ViewModelUserAdd extends AndroidViewModel {

    private final Application application;
    private final long userId;
    private User user;
    public ViewModelUserAdd(Application app, long userId)
    {
        super(app);
        application = app;
        this.userId = userId;

        if (this.userId > 0)
        {
            this.user = Operators.getInstance().find(userId);
            if (this.user == null) this.user = Supervisors.getInstance().find(userId);
        }
    }

    @Nullable public User getUser() { return this.user;}


    public void saveUser(String fn, String ln, String pin, boolean enabled,  Role role,  Runnable oncomplete)
    {

        TableUser user = new TableUser();

        user.first_name = fn;
        user.last_name = ln;
        user.pin = pin;
        user.enabled =enabled ? 1 : 0;
        user.deleted  = 0;
        user.id = this.userId <= 0 ? 0 : this.userId;
        user.role_id = role.getId();
        user.updated_datetime = (new Date()).getTime();
        user.created_datetime = (new Date()).getTime();

        DaoUser daoUser = PrestartCheckDatabase.getDatabase(this.application).getDaoUser();

        PrestartCheckDatabase.getDatabaseWriteExecutor().execute(new Runnable() {
            @Override
            public void run() {
                daoUser.insertUser(user);
                oncomplete.run();
            }
        });

    }
}
