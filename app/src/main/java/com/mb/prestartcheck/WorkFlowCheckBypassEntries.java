package com.mb.prestartcheck;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class WorkFlowCheckBypassEntries implements  Machine.MachineCommsListener ,
        Machine.MachineInterlockDeviceMemoryReadListener, Machine.MachineInterlockStateDeviceListener {

    private Machine machine = new Machine();
    private Context context;

    public  void run(Context ctx, String deviceAddr, String finsNodeNumber)
    {
        AppLog.getInstance().print("-----Started WorkFlowCheckBypassEntries. Emailing bypasses.");

        this.context = ctx;
        this.machine.initComms(this, deviceAddr, finsNodeNumber);
    }



    @Override
    public void onMachineInterlockDeviceStateRead(boolean isClosed) {

    }

    @Override
    public void onMachineInitComms(Boolean connected) {
        if (connected) this.machine.getBypassEntries(this);
    }

    @Override
    public void onMachineBypassReadCompleted(Object[] lines) {
        try {
            this.machine.endComms();

            AppLog.getInstance().print("WorkFlowCheckBypassEntries onMachineBypassReadCompleted. %d lines.", lines.length);

            String filename = FileName.get("bypass");

            FileOutputStream fileOutputStream = context.openFileOutput(filename, 0);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            for (Object line : lines) {
                ReportLineByPass reportLineByPass = (ReportLineByPass)line;
                outputStreamWriter.write(ReportLineByPass.dateFormatBCD.format(reportLineByPass.getDateTime()));
                outputStreamWriter.write(",");
                outputStreamWriter.write(Integer.toString(reportLineByPass.getEventType()));
                outputStreamWriter.write('\n');

            }

            outputStreamWriter.close();

            //Send the responses as an email attachment using smtp.
            String subject = String.format("'%s' prestart check bypass report. See attachment.", Machine.getInstance().getName());
            Settings settings = App.getInstance().getSettings();
            ArrayList<String> recipients = new ArrayList<String>();
            String attachmentFullPath = context.getFilesDir() + "/" + filename;

            if (!settings.get(Settings.BYPASS_REPORT_RECIPIENT_ONE).isEmpty())
                recipients.add(settings.get(Settings.BYPASS_REPORT_RECIPIENT_ONE));

            if (!settings.get(Settings.BYPASS_REPORT_RECIPIENT_TWO).isEmpty())
                recipients.add(settings.get(Settings.BYPASS_REPORT_RECIPIENT_TWO));

            if (!settings.get(Settings.BYPASS_REPORT_RECIPIENT_THREE).isEmpty())
                recipients.add(settings.get(Settings.BYPASS_REPORT_RECIPIENT_THREE));

            if (!settings.get(Settings.BYPASS_REPORT_RECIPIENT_FOUR).isEmpty())
                recipients.add(settings.get(Settings.BYPASS_REPORT_RECIPIENT_FOUR));

            String[] tmp = new String[recipients.size()];
            recipients.toArray(tmp);
            boolean sent = Emailer.sendWithAttachment(attachmentFullPath, subject, tmp);

            if (sent)
                AppLog.getInstance().print("WorkerEmailResponse sent email.");
            else
                AppLog.getInstance().print("WorkerEmailResponse failed to send email");
        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }


}
