package com.mb.prestartcheck.fins;

public class LogEvents {
    private static LogEvents instance;

    public static LogEvents getInstance() {
        if (instance == null) instance = new LogEvents();
        return instance;
    }

    public LogEvents() {
    }

    /**
     * User administration
     * Any changes to user values should be recorded in a log file (text) - including
     * Date
     * Time
     * Whom (supervisor username)
     * What change (Before / After)
     */

    public String EVENT_USER_ADMIN_DATE = "";
    public String EVENT_USER_ADMIN_TIME = "";
    public String EVENT_USER_ADMIN_WHOM = "";
    public String EVENT_USER_ADMIN_CHANGE_BEFORE = "";
    public String EVENT_USER_ADMIN_CHANGE_AFTER = "";

    /**
     * FragmentLogin
     * A user logging into the application
     * Date
     * time
     * user
     * action
     * result
     */

    public String EVENT_USER_LOGIN_DATE = "";
    public String EVENT_USER_LOGIN_TIME = "";
    public String EVENT_USER_LOGIN_USER = "";
    public String EVENT_USER_LOGIN_ACTION = "";
    public String EVENT_USER_LOGIN_RESULT = "";


    /* Any question that can operate the machine.For an administrator, the " FragmentInterlockDevice" class.
        A user being prompted to deactivate the interlock ( Machine running)
     Date and time, user, action, result*/
    public String EVENT_DEACTIVATION_INTERLOCK_DATE = "";
    public String EVENT_DEACTIVATION_INTERLOCK_TIME = "";
    public String EVENT_DEACTIVATION_INTERLOCK_USER = "";
    public String EVENT_DEACTIVATION_INTERLOCK_ACTION = "";
    public String EVENT_DEACTIVATION_INTERLOCK_RESULT = "";

    /*  Any question that can operate the machine.For an administrator, the " FragmentInterlockDevice" class.
        A user accepting the unlock of the interlock. (  Machine running)
        Date and time, user, action, result
        */
    public String EVENT_ACTIVATION_INTERLOCK_DATE = "";
    public String EVENT_ACTIVATION_INTERLOCK_TIME = "";
    public String EVENT_ACTIVATION_INTERLOCK_USER = "";
    public String EVENT_ACTIVATION_INTERLOCK_ACTION = "";
    public String EVENT_ACTIVATION_INTERLOCK_RESULT = "";

    /*FragmentLogin
    A supervisor logging into the system	Date and time, user, action, result*/
     public String EVENT_SUPERVISOR_LOGIN_DATE = "";
    public String EVENT_SUPERVISOR_LOGIN_TIME = "";
    public String EVENT_SUPERVISOR_LOGIN_USER = "";
    public String EVENT_SUPERVISOR_LOGIN_ACTION = "";
    public String EVENT_SUPERVISOR_LOGIN_RESULT = "";



  /* FragmentSummary.
   Any administration fragment.
    A supervisor reviewing a user's errors and unlocking the interlock	Date and time, user, action, result*/
    public String EVENT_SUPERVISOR_UNLOCKING_INTERLOCK_DATE = "";
    public String EVENT_SUPERVISOR_SUPERVISOR_UNLOCKING_INTERLOCK_TIME = "";
    public String EVENT_SUPERVISOR_SUPERVISOR_UNLOCKING_INTERLOCK_USER = "";
    public String EVENT_SUPERVISOR_SUPERVISOR_UNLOCKING_INTERLOCK_ACTION = "";
    public String EVENT_SUPERVISOR_SUPERVISOR_UNLOCKING_INTERLOCK_RESULT = "";


    /*Any administration fragment.Question administration fragments.Section administration fragments.Reporting segments.
    A supervisor logging into the admin section	Date and time, user, action, result*/
    public String EVENT_ADMIN_SUPERVISOR_LOGIN_DATE = "";
    public String EVENT_ADMIN_SUPERVISOR_LOGIN_TIME = "";
    public String EVENT_ADMIN_SUPERVISOR_LOGIN_USER = "";
    public String EVENT_ADMIN_SUPERVISOR_LOGIN_ACTION = "";
    public String EVENT_ADMIN_SUPERVISOR_LOGIN_RESULT = "";


   /* Fragment logout.
       Exit of the application	Date and time, user, action, result	Fragment logout.
*/
   public String EVENT_LOGOUT_DATE = "";
    public String EVENT_LOGOUT_TIME = "";
    public String EVENT_LOGOUT_USER = "";
    public String EVENT_LOGOUT_ACTION = "";
    public String EVENT_LOGOUT_RESULT = "";


/*
     Any changes / additions to values in the admin section. This includes but is not limited to:
    * Machine Setup
    * Sections Setup
    * Question Setup
    * Reporting values
    * Notification values
    Date and time, user, action, result
    Any administration fragment.
    Question administration fragments.
    Section administration fragments.
    Reporting segments.
     Machine Setup
    Sections Setup
    * Question Setup
    * Reporting values
    * Notification values
    */


}
