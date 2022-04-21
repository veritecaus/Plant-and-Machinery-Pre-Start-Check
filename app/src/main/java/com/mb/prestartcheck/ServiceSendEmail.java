package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import com.sun.mail.handlers.text_plain;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


public class ServiceSendEmail {

    public static final int SMTP_SECURITY_TYPE_NONE =0x00;
    public static final int SMTP_SECURITY_TYPE_SSL =0x01;
    public static final int SMTP_SECURITY_TYPE_TLS =0x02;


    private  static ServiceSendEmail instance = null;

    private javax.mail.Session session;
    private Properties properties = null;

    private String smtp;
    private int port;
    private int securityType = SMTP_SECURITY_TYPE_SSL;
    private String username;
    private String password;
    private String subject;
    private String body;
    private String from;
    private String attachmentFileName;

    private boolean useSignIn = false;

    ArrayList<String> recipients = new ArrayList<String>();

    public ServiceSendEmail() {

    }

    public String getSmtp() { return this.smtp;}
    public void setSmtp(final String s) { this.smtp = s;}

    public int getSecurityType() { return this.securityType;}
    public void setSecurityType(final int st) { this.securityType = st;}

    public int getPort() { return this.port;}
    public void setPort(final int p) { this.port = p;}

    public String getUsername() { return this.username;}
    public void setUsername(final String s) { this.username = s;}

    public String getPassword() { return this.password;}
    public void setPassword(final String s) { this.password = s;}

    public String getSubject() { return this.subject;}
    public void setSubject(final String s) { this.subject = s;}

    public String getBody() { return this.body;}
    public void setBody(final String b) { this.body = b;}

    public String getFrom() { return this.from;}
    public void setFrom(final String f) { this.from = f;}

    public void addRecipient(String eaddr)
    {
        recipients.add(eaddr);
    }
    public void clearRecipients() { this.recipients.clear();};

    public String getAttachmentFileName() { return this.attachmentFileName;}
    public void setAttachmentFileName(final String fn) { this.attachmentFileName = fn;}

    public boolean isUseSignIn() {  return useSignIn; }
    public  void setUseSignIn(boolean use) { useSignIn = use;}

    public void init()
    {

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");

        CommandMap.setDefaultCommandMap(mc);

        properties = new Properties();

        properties.put("mail.smtp.host", this.smtp);
        properties.put("mail.smtp.port", this.port);
        properties.put("mail.smtp.auth", this.useSignIn ? "true" : "false");

        if (this.securityType == SMTP_SECURITY_TYPE_TLS)
            properties.put("mail.smtp.starttls.enable", "true");
        else if (this.securityType == SMTP_SECURITY_TYPE_SSL)
            properties.put("mail.smtp.startssl.enable", "true");

        session = javax.mail.Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ServiceSendEmail.this.username, ServiceSendEmail.this.password);
                    }
                });

    }


    public void sendMail()
    {
        try {


            text_plain tp = new text_plain();

            if (recipients.size() == 0) return;

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            String r = String.join(",", this.recipients);

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(r));
            message.setSubject(this.subject);
            message.setText(this.body);
            boolean removeAttachmentFile = false;

            //attachment
            if (null!= attachmentFileName) {
                if (!this.attachmentFileName.isEmpty()) {
                    File file = new File(this.attachmentFileName);
                    if (file.exists()) {
                        Multipart multipart = new MimeMultipart();
                        BodyPart textBodyPart = new MimeBodyPart();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }


                        textBodyPart.setDataHandler(new DataHandler(sb.toString(), "text/pain"));
                        textBodyPart.setHeader("Content-ID", "<text>");
                        textBodyPart.setFileName("responses.csv");

                        multipart.addBodyPart(textBodyPart);
                        message.setContent(multipart);
                        removeAttachmentFile = true;
                    }
                }
            }
            Transport.send(message);
            if (null!= attachmentFileName) {
                if (removeAttachmentFile) {
                    File file = new File(this.attachmentFileName);
                    file.delete();
                }
            }
        }
        catch(Exception mex)
        {
            Log.e(App.TAG, mex.getMessage());
        }



    }


    public static synchronized  ServiceSendEmail getInstance()
    {
        if (instance == null) instance = new ServiceSendEmail();

        return instance;

    }


}
