package com.mb.prestartcheck;

import android.util.Log;

import java.util.TimerTask;

public class QuestionTimeoutTaskTimer extends TimerTask {

    public interface  QuestionTimeoutTaskListener
    {
        void questionTimeoutCompleted();
        void questionTimeoutCanceled();
    }

    private boolean canceled = false;
    private QuestionTimeoutTaskListener listener;
    private final long timeout;

    public boolean getCanceled() { return this.canceled;}

    public void setCanceled(boolean e) {
        this.canceled = e;
        if (this.canceled && listener != null) this.listener.questionTimeoutCanceled();
    }

    @Override
    public void run() {
            try
            {

                Thread.sleep(timeout*1000);
                if (!this.canceled && listener != null)  listener.questionTimeoutCompleted();

            }
            catch (Exception ex)
            {
                AppLog.getInstance().print(ex.getMessage());
            }
    }

    public  QuestionTimeoutTaskTimer(QuestionTimeoutTaskListener e, long seconds)
    {
        listener = e;
        this.canceled = false;
        timeout = seconds;
    }
}
