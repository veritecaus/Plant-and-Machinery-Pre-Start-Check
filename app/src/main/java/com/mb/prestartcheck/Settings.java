package com.mb.prestartcheck;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mb.prestartcheck.data.DaoSettings;
import com.mb.prestartcheck.data.DaoUser;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestionType;
import com.mb.prestartcheck.data.TableSettings;
import com.mb.prestartcheck.data.TableSettingsUpdate;
import com.mb.prestartcheck.data.TableUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Settings {

    public interface onSyncListener
    {
        void syncCompleted();
    }

    public interface OnSavedHandler
    {
        void saved();
    }


    private Hashtable<String,String> hash = new Hashtable<String,String>() ;
    private Hashtable<Context, Settings.onSyncListener> listeners = new Hashtable<Context, Settings.onSyncListener>();
    private static final String Preferences_File_Name = "PREFERENCE_FILE_KEY";

    public static final String EMAIl_TO = "email_to";
    public static final String EMAIl_FROM = "email_from";
    public static final String EMAIl_SMTP = "email_smtp";
    public static final String EMAIl_SMTP_PORT = "email_smtp_port";
    public static final String EMAIl_REQUIRES_SIGNIN = "email_requires_sign_in";
    public static final String EMAIL_USER_NAME = "email_user_name";
    public static final String EMAIL_USER_PASSWORD = "email_password";
    public static final String EMAIL_SECURITY_TYPE = "email_security_type";
    public static final String MACHINE_NAME = "machine_name";

    public static final String REPORT_RECIPIENT_ONE = "report_recipient_one";
    public static final String REPORT_RECIPIENT_TWO = "report_recipient_two";
    public static final String REPORT_RECIPIENT_THREE = "report_recipient_three";
    public static final String REPORT_RECIPIENT_FOUR = "report_recipient_four";
    public static final String REPORT_RECIPIENT_FREQUENCY = "report_recipient_frequency";

    public static final String BYPASS_REPORT_RECIPIENT_ONE = "bypass_report_recipient_one";
    public static final String BYPASS_REPORT_RECIPIENT_TWO = "bypass_report_recipient_two";
    public static final String BYPASS_REPORT_RECIPIENT_THREE = "bypass_report_recipient_three";
    public static final String BYPASS_REPORT_RECIPIENT_FOUR = "bypass_report_recipient_four";
    public static final String BYPASS_REPORT_RECIPIENT_FREQUENCY = "bypass_report_recipient_frequency";

    public static final String INTERLOCK_DEVICE_ADDRESS = "interlock_device_address";
    public static final String EMAIL_RESPONSES = "email_responses";
    public static final String EMAIL_BYPASSES = "email_bypasses";
    public static final String INTERLOCK_DEVICE_FIN_NODE_ADDRESS = "interlock_device_fin_node_address";
    public static final String INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS = "interlock_device_fin_alt_node_address";
    public static final String COMPANY_LOGO = "company_logo";

    public static final String TIMEOUT_MACHINE_OPERATING_HOURS = "timeout_machine_operting_hours";

    public Settings()
    {

    }

    public void add(String key, String value)
    {
        if (!this.hash.containsKey(key))  this.hash.put(key, value);
    }

    public  void set(String key, String value)
    {
        if (this.hash.containsKey(key))  this.hash.put(key, value);

    }

    public String get(String key)
    {
        return hash.containsKey(key) ?  hash.get(key) : "";
    }
    public  boolean contains(String key)
    {
        return hash.containsKey(key);
    }
    public   int size() { return hash.size();}

    public int getSize()
    {
        return this.hash.size();
    }

    public  void clear()
    {
        hash.clear();
    }

    public void addListener(Context c,  Settings.onSyncListener e)
    {
        if (!listeners.contains(c))
            listeners.put(c, e);
    }

    public  void removeListener(Context c)
    {
        listeners.remove(c);
    }

    public  void addObserver(AppCompatActivity app)
    {
        DaoSettings daoSettings = PrestartCheckDatabase.getDatabase(app).getDaoSettings();
        if (!daoSettings.getAll().hasObservers())
        {
            daoSettings.getAll().observe(app, rows->{

                Settings.sync(rows, this);

                Log.i(App.TAG, String.format("synced settings, and found %d.", this.hash.size()));
                for(Enumeration<Settings.onSyncListener> e = listeners.elements(); e.hasMoreElements();)
                    e.nextElement().syncCompleted();

            });
        }
    }

    public  static void sync(List<TableSettings> src, Settings dest)
    {
        for(TableSettings row : src)
        {
            if (!dest.contains(row.key))
                dest.add(row.key, row.value);
            else
            {
                dest.set(row.key, row.value);

            }
        }

    }

    public static void saveToDatabase(Settings settings, Context context, OnSavedHandler handler)
    {
        /*
        PrestartCheckDatabase prestartCheckDatabase = PrestartCheckDatabase.getDatabase(context);
        DaoSettings daoSettings = prestartCheckDatabase.getDaoSettings();

        PrestartCheckDatabase.getDatabaseWriteExecutor().execute(new Runnable() {
            @Override
            public void run() {

                Date now = new  Date();
                ArrayList<TableSettingsUpdate> updates = new ArrayList<TableSettingsUpdate>();

                for (Enumeration<String> e = settings.hash.keys(); e.hasMoreElements();)
                {
                    String key = e.nextElement();

                    TableSettingsUpdate tmp = new TableSettingsUpdate();


                    tmp.key = key;
                    tmp.value = settings.hash.get(key);
                    tmp.updated_datetime = now;
                    updates.add(tmp);

                }
                daoSettings.update(updates);

                try {Thread.sleep(1000);} catch(InterruptedException iex) {}

                if (handler != null) handler.saved();

            }
        });

         */
        saveToPreferences(settings, context, handler);
    }

    private static void saveToPreferences(Settings settings, Context context, OnSavedHandler handler)
    {
        SharedPreferences preferences  =context.getSharedPreferences(context.getPackageName() + "."+ Preferences_File_Name,
                Context.MODE_PRIVATE);

            SharedPreferences.Editor editor =  preferences.edit();
            editor.putString(EMAIl_FROM, settings.hash.get(EMAIl_FROM));
            editor.putString(EMAIl_SMTP, settings.hash.get(EMAIl_SMTP));
            editor.putString(EMAIl_SMTP_PORT, settings.hash.get(EMAIl_SMTP_PORT));
            editor.putString(EMAIl_REQUIRES_SIGNIN, settings.hash.get(EMAIl_REQUIRES_SIGNIN));
            editor.putString(EMAIL_USER_NAME, settings.hash.get(EMAIL_USER_NAME));
            editor.putString(EMAIL_USER_PASSWORD, settings.hash.get(EMAIL_USER_PASSWORD));
            editor.putString(EMAIL_SECURITY_TYPE, settings.hash.get(EMAIL_SECURITY_TYPE));
            editor.putString(MACHINE_NAME, settings.hash.get(MACHINE_NAME));
            editor.putString(REPORT_RECIPIENT_ONE, settings.hash.get(REPORT_RECIPIENT_ONE));
            editor.putString(REPORT_RECIPIENT_TWO, settings.hash.get(REPORT_RECIPIENT_TWO));
            editor.putString(REPORT_RECIPIENT_THREE, settings.hash.get(REPORT_RECIPIENT_THREE));
            editor.putString(REPORT_RECIPIENT_FOUR, settings.hash.get(REPORT_RECIPIENT_FOUR));
            editor.putString(REPORT_RECIPIENT_FREQUENCY, settings.hash.get(REPORT_RECIPIENT_FREQUENCY));

            editor.putString(BYPASS_REPORT_RECIPIENT_ONE, settings.hash.get(BYPASS_REPORT_RECIPIENT_ONE));
            editor.putString(BYPASS_REPORT_RECIPIENT_TWO, settings.hash.get(BYPASS_REPORT_RECIPIENT_TWO));
            editor.putString(BYPASS_REPORT_RECIPIENT_THREE, settings.hash.get(BYPASS_REPORT_RECIPIENT_THREE));
            editor.putString(BYPASS_REPORT_RECIPIENT_FOUR, settings.hash.get(BYPASS_REPORT_RECIPIENT_FOUR));
            editor.putString(BYPASS_REPORT_RECIPIENT_FREQUENCY, settings.hash.get(BYPASS_REPORT_RECIPIENT_FREQUENCY));

            editor.putString(INTERLOCK_DEVICE_ADDRESS, settings.hash.get(INTERLOCK_DEVICE_ADDRESS));
            editor.putString(EMAIL_RESPONSES, settings.hash.get(EMAIL_RESPONSES));
            editor.putString(EMAIL_BYPASSES, settings.hash.get(EMAIL_BYPASSES));
            editor.putString(INTERLOCK_DEVICE_FIN_NODE_ADDRESS, settings.hash.get(INTERLOCK_DEVICE_FIN_NODE_ADDRESS));
            editor.putString(INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS, settings.hash.get(INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS));
            editor.putString(COMPANY_LOGO, settings.hash.get(COMPANY_LOGO));
            editor.putString(TIMEOUT_MACHINE_OPERATING_HOURS, settings.hash.get(TIMEOUT_MACHINE_OPERATING_HOURS));

            if (!editor.commit())
            {
                AppLog.getInstance().print("Error commit changes to Preferences!.");
            }
            else
                AppLog.getInstance().print(" Preferences saved!.");

            if (handler!= null) handler.saved();


    }

    public  void  readFromAppPreferences(Context context)
    {
        SharedPreferences preferences  =context.getSharedPreferences(context.getPackageName() + "."+ Preferences_File_Name,
                                    Context.MODE_PRIVATE);
        hash.clear();
        hash.put(EMAIl_TO, preferences.getString(EMAIl_TO,"nobody@nowhere.com"));
        hash.put(EMAIl_FROM, preferences.getString(EMAIl_FROM,"noreply@default.com"));
        hash.put(EMAIl_SMTP,  preferences.getString(EMAIl_SMTP, ""));
        hash.put(EMAIl_SMTP_PORT, preferences.getString(EMAIl_SMTP_PORT,"25"));
        hash.put(EMAIl_REQUIRES_SIGNIN, preferences.getString(EMAIl_REQUIRES_SIGNIN, "1"));
        hash.put(EMAIL_USER_NAME, preferences.getString(EMAIL_USER_NAME,""));
        hash.put(EMAIL_USER_PASSWORD, preferences.getString(EMAIL_USER_PASSWORD,""));
        hash.put(EMAIL_SECURITY_TYPE, preferences.getString(EMAIL_SECURITY_TYPE,"tls"));
        hash.put(MACHINE_NAME, preferences.getString(MACHINE_NAME, ""));
        hash.put(REPORT_RECIPIENT_ONE, preferences.getString(REPORT_RECIPIENT_ONE, ""));
        hash.put(REPORT_RECIPIENT_TWO, preferences.getString(REPORT_RECIPIENT_TWO, ""));
        hash.put(REPORT_RECIPIENT_THREE, preferences.getString(REPORT_RECIPIENT_THREE, ""));
        hash.put(REPORT_RECIPIENT_FOUR, preferences.getString(REPORT_RECIPIENT_FOUR, ""));
        hash.put(REPORT_RECIPIENT_FREQUENCY, preferences.getString(REPORT_RECIPIENT_FREQUENCY, "daily"));
        hash.put(BYPASS_REPORT_RECIPIENT_ONE, preferences.getString(BYPASS_REPORT_RECIPIENT_ONE,""));
        hash.put(BYPASS_REPORT_RECIPIENT_TWO, preferences.getString(BYPASS_REPORT_RECIPIENT_TWO,""));
        hash.put(BYPASS_REPORT_RECIPIENT_THREE, preferences.getString(BYPASS_REPORT_RECIPIENT_THREE,""));
        hash.put(BYPASS_REPORT_RECIPIENT_FOUR, preferences.getString(BYPASS_REPORT_RECIPIENT_FOUR,""));
        hash.put(BYPASS_REPORT_RECIPIENT_FREQUENCY,  preferences.getString(BYPASS_REPORT_RECIPIENT_FOUR,"daily"));
        hash.put(INTERLOCK_DEVICE_ADDRESS, preferences.getString(INTERLOCK_DEVICE_ADDRESS, "192.168.250.1"));
        hash.put(EMAIL_RESPONSES, preferences.getString(EMAIL_RESPONSES, "true"));
        hash.put(EMAIL_BYPASSES, preferences.getString(EMAIL_BYPASSES, "true"));
        hash.put(INTERLOCK_DEVICE_FIN_NODE_ADDRESS, preferences.getString(INTERLOCK_DEVICE_FIN_NODE_ADDRESS, "200"));
        hash.put(INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS, preferences.getString(INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS, "190"));
        hash.put(COMPANY_LOGO, preferences.getString(COMPANY_LOGO,""));
        hash.put(TIMEOUT_MACHINE_OPERATING_HOURS, preferences.getString(TIMEOUT_MACHINE_OPERATING_HOURS,"8"));


    }
}



