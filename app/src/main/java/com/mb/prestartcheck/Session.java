package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import com.mb.prestartcheck.console.ViewModelFragmentLogout;
import com.mb.prestartcheck.data.DaoSession;
import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableSession;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

public class Session  {
    private long id;
    private User user;
    private String uuid;
    private Date loginDateTime;
    private Date logoutDateTime;
    private Date createdDateTime;
    private Date updatedDateTime;

    private ImageLocal lastSelectedImage;
    private ParameterImageSelect lastImageSelectionParameters;

    private static Session instance;

    private Timer timer;
    private final String TIMER_TAG = "TIMER_MONITOR_INTERLOCK_DEVICE";


    public  interface InterlockDeviceMonitorListener
    {
        void interlockDeviceMonitorIsOpen();
    }

    public  static Session getInstance() {
        if (instance == null)  {  instance = new Session(); }
        return instance;
    }

    public void setId(long id) {  this.id = id; }
    public long getId()  { return this.id;}

    public void setUser(User val) {  this.user = val;}
    public User getUser() { return this.user;}

    public String getUuid() { return this.uuid;}
    public void setUuid(String e) { this.uuid = e;}

    public Date getLoginDateTime() { return this.loginDateTime;}
    public void setLoginDateTime(Date e) { this.loginDateTime = e;}

    public Date getLogoutDateTime() { return this.logoutDateTime;}
    public void setLogoutDateTime(Date e) { this.logoutDateTime = e;}

    public Date getCreatedDateTime() { return this.createdDateTime;}
    public void setCreatedDateTime(Date e) { this.createdDateTime = e;}

    public Date getUpdatedDateTime() { return this.updatedDateTime;}
    public void setUpdatedDateTime(Date e) { this.updatedDateTime = e;}

    public ImageLocal getLastSelectedImage() { return lastSelectedImage;}
    public void setLastSelectedImage(ImageLocal imageLocal) { this.lastSelectedImage = imageLocal;}


    public ParameterImageSelect getLastImageSelectionParameters()  { return lastImageSelectionParameters;}
    public void  setLastImageSelectionParameters(ParameterImageSelect p)  {  lastImageSelectionParameters = p;}
    public  void clearLastParameterImageSelection() { lastImageSelectionParameters= null; }

    public  boolean hasPendingImageSelection() {
        return lastImageSelectionParameters != null && lastImageSelectionParameters.getUri() != null
                && !lastImageSelectionParameters.getUri().isEmpty() && lastImageSelectionParameters.getThumbnail() != null;
    }

    public  Session()
    {

    }

    public static TableSession toTableSession(Session session)
    {
        TableSession tblSession = new TableSession();
        tblSession.id = session.getId();
        tblSession.operator_id = session.getUser().getId();
        tblSession.uuid = session.getUuid();
        tblSession.login_date_time  = session.getLoginDateTime().getTime();
        tblSession.logout_date_time = session.getLogoutDateTime().getTime();
        tblSession.created_datetime = session.getCreatedDateTime().getTime();
        tblSession.updated_datetime = session.getUpdatedDateTime().getTime();
        tblSession.deleted = 0;

        return tblSession;
    }

    public void login(User user)
    {
        this.user = user;
        this.loginDateTime = new Date();
        this.logoutDateTime = new Date();
        this.uuid  = UUID.randomUUID().toString();
        Date now = new Date();
        this.createdDateTime = now;
        this.updatedDateTime = now;
        this.id = 0;
        lastImageSelectionParameters = null;

    }

    public  void logout(Context context, Runnable onLogout)
    {


        PrestartCheckDatabase prestartCheckDatabase = PrestartCheckDatabase.getDatabase(context);
        DaoSession daoSession = prestartCheckDatabase.getDaoSession();

        prestartCheckDatabase.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //Log exit of the application.
                AppLog.getInstance().reportEvent(Session.getInstance().getUser().getFullName(), (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())), ActionsEnum.EVENT_LOGOUT_ACTION.name(), ResultEnum.RESULT_SUCCESS.name());

                Date now = new Date();

                daoSession.logout(now.getTime(), Session.getInstance().getId());

                Session.this.user = null;
                Session.this.uuid = "";
                Session.this.id = 0;
                Session.this.lastImageSelectionParameters = null;

                Questioner.getInstance().getQuestionerState().setUser(null);
                Questioner.getInstance().restart(false);

                onLogout.run();

            }
        });

    }

    public boolean isSupervisorLoggedIn()
    {
        return this.user != null &&  Supervisor.class.isAssignableFrom(this.user.getClass());
    }

    public  void startInterlockDeviceMonitoring(final InterlockDeviceMonitorListener listener)
    {
        if (timer != null)
        {
            AppLog.getInstance().print("An attempt to start an existing interlock device timer was made.");
            timer.cancel();
            timer.purge();
            timer = null;
        }

        this.timer = new Timer(TIMER_TAG);

        timer.schedule(new MonitorInterlockDeviceTaskTimer(new MonitorInterlockDeviceTaskTimer.MonitorInterlockDeviceListener() {
            @Override
            public void monitorInterlockDeviceOpened() {
                timer.cancel();
                if (listener != null) listener.interlockDeviceMonitorIsOpen();

            }
        }), 30000, 30000);

    }

    public  void stopInterlockDeviceMonitoring()
    {
        try {

            if (this.timer != null)
            {
                this.timer.cancel();
                this.timer.purge();
                this.timer = null;
            }


        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


}
