package com.mb.prestartcheck;

import com.mb.prestartcheck.fins.FINSUtils;

import java.util.Date;

public class ReportLineByPass {

    private int seconds;
    private int minute;
    private int hour;
    private int day;
    private int month;
    private int year;
    private int eventType;

    public static DateFormatBCD dateFormatBCD = new DateFormatBCD();

    private Date datetime;

    public ReportLineByPass()
    {

    }
    public ReportLineByPass(byte[] bytes)
    {
        deserialize(bytes, 0);
    }

    public ReportLineByPass(byte[] bytes, int offset)
    {
        deserialize(bytes, offset);
    }



    public int getSeconds() {  return seconds; }
    public int getMinute() {  return minute; }
    public int getHour() {  return hour; }
    public int getDay() {  return day; }
    public int getMonth() {  return month; }
    public int getYear() {  return year; }
    public int getEventType() {  return eventType; }
    public Date getDateTime() {  return datetime; }


    protected  void deserialize(byte[] bytes, int offset)
    {
        try {
            //Seconds in binary coded decimal bytes[1]
            int[] bcdSeconds = FINSUtils.get2DigitBCDFromByte(bytes[offset+ 1]); //seconds
            int[] bcdMin = FINSUtils.get2DigitBCDFromByte(bytes[offset+0]);  //minutes
            int[] bcdHour = FINSUtils.get2DigitBCDFromByte(bytes[offset+3]); //hour
            int[] bcdDay = FINSUtils.get2DigitBCDFromByte(bytes[offset+2]); //day
            int[] bcdMonth = FINSUtils.get2DigitBCDFromByte(bytes[offset+5]); //month
            int[] bcdYear = FINSUtils.get2DigitBCDFromByte(bytes[offset+4]); //year
            //byte bcdEventTypeMSB  = bytes[offset+6];
            this.eventType = (int)(bytes[offset+7]);

            this.seconds = Integer.parseInt(String.format("%d%d", bcdSeconds[1], bcdSeconds[0]));
            this.minute = Integer.parseInt(String.format("%d%d", bcdMin[1], bcdMin[0]));
            this.hour = Integer.parseInt(String.format("%d%d", bcdHour[1], bcdHour[0]));
            this.day = Integer.parseInt(String.format("%d%d", bcdDay[1], bcdDay[0]));
            this.month = Integer.parseInt(String.format("%d%d", bcdMonth[1], bcdMonth[0]));
            this.year = Integer.parseInt(String.format("%d%d", bcdYear[1], bcdYear[0]));

            String sdate = String.format("%02d/%02d/%02d %02d:%02d:%02d", this.day,  this.month, this.year, this.hour, this.minute, this.seconds);
            this.datetime = ReportLineByPass.dateFormatBCD.parse(sdate);


        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

}
