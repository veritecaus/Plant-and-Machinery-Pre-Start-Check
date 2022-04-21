package com.mb.prestartcheck;

public class TimeSpan  {
    private int days;

    public int getDays() { return days;}
    void setDays(int days) { this.days = days; }

    public TimeSpan(String str)
    {
        this.days = 0;
        if (str != null && !str.isEmpty())
         {
            if (str.compareToIgnoreCase("daily") == 0)
                this.days = 1;
            else if (str.compareToIgnoreCase("weekly") == 0)
                this.days = 7;
            else if (str.compareToIgnoreCase("monthly") == 0)
                this.days = 30;
        }
    }
}
