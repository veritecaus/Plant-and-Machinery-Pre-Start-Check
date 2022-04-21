package com.mb.prestartcheck;

import android.content.Context;

import com.mb.prestartcheck.console.UIValidator;

import java.io.File;

public class Emailer {

    public static boolean sendWithAttachment(String attachment, String subject, String[] recipients) {
        Settings settings = App.getInstance().getSettings();

        ServiceSendEmail serviceSendEmail = ServiceSendEmail.getInstance();
        serviceSendEmail.setSmtp(settings.get(Settings.EMAIl_SMTP));

        Integer port = UIValidator.safeGetInteger(settings.get(Settings.EMAIl_SMTP_PORT));

        if (settings.get(Settings.EMAIl_SMTP_PORT) == "tls") {
            serviceSendEmail.setSecurityType(ServiceSendEmail.SMTP_SECURITY_TYPE_TLS);
            port = port == null ? 587 : port; //Default to using the standard ssl smtp port.
        } else if (settings.get(Settings.EMAIl_SMTP_PORT) == "ssl") {

            serviceSendEmail.setSecurityType(ServiceSendEmail.SMTP_SECURITY_TYPE_SSL);
            port = port == null ? 587 : port; //Default to using the standard ssl smtp port.
        } else {
            serviceSendEmail.setSecurityType(ServiceSendEmail.SMTP_SECURITY_TYPE_NONE);
            port = port == null ? 25 : port; //Default to using the standard ssl smtp port.

        }

        Boolean signIn = Boolean.parseBoolean(settings.get(Settings.EMAIl_REQUIRES_SIGNIN));


        serviceSendEmail.setPort(port.intValue());
        serviceSendEmail.setUseSignIn(signIn);

        serviceSendEmail.setUsername(settings.get(Settings.EMAIL_USER_NAME));
        serviceSendEmail.setPassword(settings.get(Settings.EMAIL_USER_PASSWORD));

        serviceSendEmail.clearRecipients();

        for (String r : recipients)
            serviceSendEmail.addRecipient(r);

        serviceSendEmail.setSubject(subject);
        if (null != attachment) {
            serviceSendEmail.setBody("See attached email.");
            serviceSendEmail.setAttachmentFileName(attachment);
        } else {
            serviceSendEmail.setBody("This is a test email.");
        }
        serviceSendEmail.setFrom(settings.get(Settings.EMAIl_FROM));
        serviceSendEmail.init();
        serviceSendEmail.sendMail();

        return true;
    }
}
