package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import com.mb.prestartcheck.console.UIValidator;
import com.mb.prestartcheck.data.DaoResponse;
import com.mb.prestartcheck.data.PrestartCheckDatabase;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//import javax.mail.MessagingException;


public class WorkFlowEmailReponses {

    private final String FILE_PREFIX = "email_responses";

    public void run(Context context) throws IOException
    {
        run(context, true);
    }
     public void run(Context context, boolean updatedExported) throws IOException
    {
            AppLog.getInstance().print("-----Started WorkFlowEmailReponses. Emailing responses.");

            if (!validateParameters()) {
                return ;
            }


            //Write all responses that were not sent via email to a file in the private scoped
            //directory.

            String filename = FileName.get(FILE_PREFIX);
            String attachmentFullPath = context.getFilesDir() + "/" + filename;
            File file = new File(attachmentFullPath);

            Reporter reporter = new Reporter(Machine.getInstance(), Session.getInstance().getUser());
            int noResponses = reporter.publishReponsesToFile(context, file);
            AppLog.getInstance().print("WorkerEmailResponse found %d responses.", noResponses);

            if (noResponses > 0) {
                //Send the responses as an email attachment using smtp.
                String subject = String.format("'%s' prestart check responses to questions. See attachment.", Machine.getInstance().getName());
                Settings settings = App.getInstance().getSettings();
                ArrayList<String> recipients = new ArrayList<String>();

                if (!settings.get(Settings.REPORT_RECIPIENT_ONE).isEmpty())
                    recipients.add(settings.get(Settings.REPORT_RECIPIENT_ONE));

                if (!settings.get(Settings.REPORT_RECIPIENT_TWO).isEmpty())
                    recipients.add(settings.get(Settings.REPORT_RECIPIENT_TWO));

                if (!settings.get(Settings.REPORT_RECIPIENT_THREE).isEmpty())
                    recipients.add(settings.get(Settings.REPORT_RECIPIENT_THREE));

                if (!settings.get(Settings.REPORT_RECIPIENT_FOUR).isEmpty())
                    recipients.add(settings.get(Settings.REPORT_RECIPIENT_FOUR));

                String[] tmp = new String[recipients.size()];
                recipients.toArray(tmp);
                boolean sent  = Emailer.sendWithAttachment(attachmentFullPath, subject, tmp);

                if (sent)
                    AppLog.getInstance().print("WorkerEmailResponse sent email.");
                else
                    AppLog.getInstance().print("WorkerEmailResponse failed to send email");

                if (updatedExported)
                    updateResponses(context);
            }
    }

    private boolean validateParameters() {
        Settings settings = App.getInstance().getSettings();

        if (settings.get(Settings.EMAIl_SMTP).isEmpty())
        {
            Log.i(App.TAG, "Missing smtp server address.");
            return false;
        }

        if (settings.get(Settings.EMAIL_USER_NAME).isEmpty())
        {
            Log.i(App.TAG, "Missing smtp creditials.");
            return false;
        }

        if (settings.get(Settings.EMAIL_USER_PASSWORD).isEmpty())
        {
            Log.i(App.TAG, "Missing smtp creditials.");
            return false;
        }

        return true;
    }

    private void updateResponses(Context context)
    {
        PrestartCheckDatabase prestartCheckDatabase = PrestartCheckDatabase.getDatabase(context);
        DaoResponse daoResponse =  prestartCheckDatabase.getDaoResponse();
        daoResponse.setExported();
    }

}
