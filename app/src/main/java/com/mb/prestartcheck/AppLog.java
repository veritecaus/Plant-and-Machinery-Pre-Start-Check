package com.mb.prestartcheck;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLog {

    private static AppLog instance;
    String fileName = setFileName();
    String updatesValuesString = "";
    public boolean isValueUpdated = false;
    //Keep a reference to the log file output stream for logging.
    PrintWriter out;

    //This boolean indicates if the log file is writable .
    private boolean IsValidLogFile = false;

    private OutputStreamWriter outputStreamWriter;
    private DateFormaterLog dateFormaterLog = new DateFormaterLog();


    public static AppLog getInstance() {
        if (instance == null) instance = new AppLog();
        return instance;
    }


    public AppLog() {
    }

    public String setFileName() {
        fileName = "Application-log_";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String currentDateandTime = sdf.format(new Date());
        IsValidLogFile = false;
        return fileName += currentDateandTime + ".txt";
    }


    public void close() {
        try {

            if (out != null) out.close();
            IsValidLogFile = false;

        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }

    }

    public void print(String format, Object... params) {
        String ts = String.format("%s: ", dateFormaterLog.format(new Date()));
        String msg = String.format(format, params);
        try {
            out.write(ts);
            out.write(msg);
            out.write("\n");
            out.flush();
        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }
    }

    public void ReadDailyTextFile() {

        try {
            File dir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir, fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder text = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                text.append(line + "\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ioe) {
            print(ioe.getMessage());
        }
    }


    public void writeDailyTextFile(String text) {
        try {
            if (!IsValidLogFile) openLogDailyTextFile();

            //saving in public Documents directory
            out.println(text);
            //out.close();
        } catch (IOException ioe) {
            Log.e(App.TAG, ioe.getMessage());
            //If there was an error writing to the  log,
            //then reopen it next time an entry is added.
            IsValidLogFile = false;

        }
    }

    /**
     * Open the log file ( that is created daily) for writting.
     * @throws IOException
     */
    private void openLogDailyTextFile() throws IOException
    {
        //If the log  file is open from the previous day, then
        //close it.
        if (out != null) out.close();

        //saving in public Documents directory
        File dir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        out = new PrintWriter(new FileWriter(file, true));
        IsValidLogFile = true;

    }

    public void reportEvent(String user, String timeStamp, String action, String result) {
        String userLogs = action + "\n" + timeStamp + "\n" + user + "\n" + result;
        writeDailyTextFile(userLogs);
        Log.i("trackLogs", userLogs);
    }

    /*
     FragmentReport
     */
    public void reportDatesEvent(String user, String timeStamp, String action, String result, String reportingDates) {
        String userLogs = action + "\n" + timeStamp + "\n" + user + "\n" + result + "\n" + reportingDates;
        writeDailyTextFile(userLogs);
        Log.i("trackLogs", userLogs);
    }

    public void updateValuesChangeString(String title, String oldValue, String changedValue) {
        updatesValuesString = updatesValuesString + "\n" + title + ":" + "\n" + "Old Value:" + "\n" + oldValue + "\n" + "Updated Value:" + "\n" + changedValue + "\n" + "\n";
    }

    public void reportValuesChangeEvent(String user, String timeStamp, String action, String result) {
        String userLog = action + "\n" + timeStamp + "\n" + user + "\n" + result + "\n" + updatesValuesString;
        writeDailyTextFile(userLog);
        Log.i("trackLogs", userLog);
        updatesValuesString = "";
        isValueUpdated = false;
    }

    /**
     * Add an audit entry to the application log when the TCP/IP connnection to the PLC is
     * lost.
     */
    public void eventPLCTCPIPLost()
    {
        //Print the  calling method as part of the event description.
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //If the top of the stack is this method, then the calling
        //method is the next element in the stack trace.
        String eventDesc = stack.length > 1 ? stack[1].toString() :"Unknown method.";
        auditEvent(ActionsEnum.EVENT_TCP_IP_SOCKETS.name(), eventDesc, "Lost");
    }

    /**
     * Add an audit entry to the application log when the application explicitly
     * disconnects the socket communicating with the the PLC.
     */
    public void eventPLCTCPIPDisconnet()
    {
        //Create a description the event.
        String eventDesc = "Manual socket disconnect";
        auditEvent(ActionsEnum.EVENT_TCP_IP_SOCKETS.name(), eventDesc, "Disconnected.");
    }

    /**
     * Add a log entry for actions executed on  the interlock device.
     * @param action A Description of the action executed on the interlock device.
     */
    public void eventInterlockDeviceAction(String action)
    {
        auditEvent(ActionsEnum.EVENT_TCP_IP_SOCKETS.name(), action, ResultEnum.RESULT_SUCCESS.name());
    }

    /**
     * Add a log entry for changes in the "interlock device" settings screen.
     * @param setting Name of the setting to record.
     * @param newValue The value after the modification.
     * @param oldValue The value before the modification.
     */
    public void eventInterlockDeviceSettingChanged(@NonNull final String setting,
                                                   @NonNull final String newValue,
                                                   @NonNull final  String oldValue) {
        //Record a change in the interlock device settings.
        //State what was modified in the event paramater and
        //record the old and new values.
        String event =  setting + " modified.";
        audit("Interlock device setting screen", oldValue, newValue, event, ResultEnum.RESULT_SUCCESS.name());
    }


    /**
     * Add a log entry when a TCP/IP connection to the PLC was successfully estabished  .
     */
    public void eventPLCTCPIPSuccess()
    {
        //Print the  calling method as part of the event description.
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //If the top of the stack is this method, then the calling
        //method is the next element in the stack trace.
        String eventDesc = stack.length > 1 ? stack[1].toString() :"Unknown method.";
        auditEvent(ActionsEnum.EVENT_TCP_IP_SOCKETS.name(), eventDesc, "Success");
    }


    public void eventFINCTCPSuccess()
    {
        //Print the  calling method as part of the event description.
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //If the top of the stack is this method, then the calling
        //method is the next element in the stack trace.
        String eventDesc = stack.length > 1 ? stack[1].toString() :"Unknown method.";
        auditEvent(ActionsEnum.EVENT_FINS_TCP.name(), eventDesc, "Success");
    }


    /**
     *  Write an action and the results of the action to the application log.
     * @param context  The string identifing which section this log entry is referring to.
     * @param event A description of the event ( what actions occured).
     * @param result The result of the event actions.
     */
    private void auditEvent(@NonNull String context, @NonNull String event, @NonNull String result)
    {
        audit(context, "", "", event, result);
    }

    /**
     *  Append an audit en
     *  try to the application log.
     * @param context The string identifing which section this log entry is referring to.
     * @param old The string value before change
     * @param newVal The current string value.
     * @param event A description of the event ( what actions occured).
     * @param result A description of the result from event actions.
     */
    private void audit(@NonNull String context,
                       @NonNull String old,
                       @NonNull String newVal,
                       @NonNull String event,
                       @NonNull String result)
    {
        //Make datetime stamp.
        String fmtToday = DateFormaterLog.instance().format(new Date());

        //Get first and last name of the person using the application.
        User u = Session.getInstance().getUser();
        //If no one is using sytem , then make first and last name blank.
        String fname = u == null ? "" : u.getFirstName();
        String lname = u == null ? "" : u.getLastName();
        //Make the log entry.
        //Record the current user of the system.
        //Record what was changed.
        //Record the result of the change.
        String logentry = String.format("%s,%s,%s,%s,%s,%s,%s,%s", fmtToday, fname, lname, old, newVal, context, event, result);
        try {
            //Append the log entry to the log file.
            writeDailyTextFile(logentry);
        }
        catch(Exception ex)
        {
            Log.e("", ex.getMessage());
        }

    }

}
