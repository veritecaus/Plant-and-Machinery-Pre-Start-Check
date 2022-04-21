package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportLine {
    private String machineName;
    private String operatorFirstName;
    private String operatorLastName;
    private Date dateTime;
    private int sectionNumber;
    private String sectionTitle;
    private int questionNumber;
    private long questionId;
    private String questionTitle;
    private String questionText;
    private boolean isNegative;
    private String responseType;
    private String expectedResponse;
    private String operatorResponse;
    private boolean isMachineOperating;
    private boolean isAnswerReviewed;
    private String supervisorName;
    private Date overridedDate;
    private String sessionId;


    public ReportLine(String machineName,
                      String operatorFirstName,
                      String operatorLastName,
                      Date dateTime,
                      int sectionNumber,
                      String sectionTitle,
                      int questionNumber,
                      long questionId,
                      String questionTitle,
                      boolean isNeg,
                      String responseType,
                      String expectedResponse,
                      String operatorResponse,
                      boolean isMachineOperating,
                      boolean isAnswerReviewed,
                      String supervisorName,
                      Date overridedDate,
                      String sid)
    {
        this.machineName = machineName;
        this.operatorFirstName = operatorFirstName;
        this.operatorLastName = operatorLastName;
        this.dateTime = dateTime;
        this.sectionNumber = sectionNumber;
        this.questionNumber = questionNumber;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.isNegative = isNeg;
        this.responseType = responseType;
        this.expectedResponse = expectedResponse;
        this.operatorResponse = operatorResponse;
        this.isMachineOperating = isMachineOperating;
        this.isAnswerReviewed = isAnswerReviewed;
        this.supervisorName = supervisorName;
        this.overridedDate = overridedDate;
        this.sectionTitle = sectionTitle;
        this.sessionId = sid;

    }

    public String getMachineName() { return this.machineName;}
    public void setMachineName(String name) {this.machineName = name;}
    public String getOperatorFirstName() { return this.operatorFirstName;}
    public void setOperatorFirstName(String name) {this.operatorFirstName = name;}
    public String getOperatorLastName() { return this.operatorLastName;}
    public void setOperatorLastName(String name) {this.operatorLastName = name;}

    public  Date getDateTime() { return this.dateTime;}
    public  void setDateTime(Date date) { this.dateTime = date;}

    public  int getSectionNumber() { return this.sectionNumber;}
    public void setSectionNumber(int number) { this.sectionNumber = number;}

    public  int getQuestionNumber() { return this.questionNumber;}
    public void setQuestionNumber(int number) { this.questionNumber = number;}

    public  long getQuestionId() { return this.questionId;}
    public void setQuestionId(int number) { this.questionId = number;}

    public String getQuestionTitle() { return this.questionTitle;}
    public void setQuestionTitle(String title) {this.questionTitle = title;}

    public boolean isNegative()  { return this.isNegative;}
    public  void setIsNegative(boolean isNegative) { this.isNegative = isNegative;}

    public String getResponseType() { return this.responseType;}
    public void setResponseType(String type) {this.questionTitle = type;}

    public String getExpectedResponse() { return this.expectedResponse;}
    public void setExpectedResponse(String resp) {this.expectedResponse = resp;}

    public String getOperatorResponse() { return this.operatorResponse;}
    public void setOperatorResponse(String resp) {this.operatorResponse = resp;}

    public boolean isMachineOperating()  { return this.isMachineOperating;}
    public  void setIsMacineOperating(boolean isop) { this.isMachineOperating = isop;}

    public boolean isAnswerReviewed()  { return this.isAnswerReviewed;}
    public  void setIsAnswerReviewed(boolean isreviewed) { this.isMachineOperating = isreviewed;}

    public String getSupervisorName() { return this.supervisorName;}
    public void setSupervisorName(String name) {this.supervisorName = name;}

    public Date getOverridedDate() { return this.overridedDate;}
    public  void setOverridedDate(Date date) { this.overridedDate = date;}

    public String getSectionTitle() { return this.sectionTitle;}
    public void setSectionTitle(String title) {this.sectionTitle = title;}

    public String getSessionId() { return this.sessionId;}
    public void setSessionId(String sid) {this.sessionId = sid;}

    public  static ReportLine create(TableResponse tableResponse)
    {
        Section section = Sections.getInstance().find(tableResponse.section);

        return new ReportLine(tableResponse.machine,
                tableResponse.first_name,
                tableResponse.last_name,
                new Date(tableResponse.date),
                (int)tableResponse.section,
                section == null ? "" : section.getTitle(),
                tableResponse.question_sequence,
                tableResponse.question_id,
                tableResponse.question_text,
                tableResponse.isNegative == 1,
                tableResponse.response_type,
                tableResponse.expected_response,
                tableResponse.operator_response,
                tableResponse.machine_unlocked == 1,
                tableResponse.answer_reviewed == 1,
                tableResponse.cleared_by,
                new Date(tableResponse.cleared_at),
                tableResponse.session_uuid);
    }

    public  static String toCSV(ReportLine reportLine)
    {
        StringBuilder stringBuilder =new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("KK:mm:ss");

        stringBuilder.append("\"" + reportLine.getMachineName() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + reportLine.getOperatorFirstName()+ "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + reportLine.getOperatorLastName() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + simpleDateFormat.format(reportLine.getDateTime()) + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" +simpleDateFormatTime.format(reportLine.getDateTime())+ "\"");
        stringBuilder.append(",");
        stringBuilder.append(reportLine.getSectionNumber());
        stringBuilder.append(",");
        stringBuilder.append("\"" + reportLine.getSectionTitle()+ "\"");
        stringBuilder.append(",");
        stringBuilder.append(reportLine.getQuestionNumber());
        stringBuilder.append(",");
        stringBuilder.append(reportLine.getQuestionId()); //Question id
        stringBuilder.append(",");
        stringBuilder.append("\"" +( reportLine.isNegative()?"Negative" : "Positive") + "\""  );
        stringBuilder.append(",");
        stringBuilder.append("\"" +reportLine.getQuestionTitle() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + reportLine.getResponseType() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" +reportLine.getExpectedResponse() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + reportLine.getOperatorResponse() + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + Boolean.toString(reportLine.isMachineOperating()) + "\"");
        stringBuilder.append(",");
        stringBuilder.append("\"" + Boolean.toString(reportLine.isAnswerReviewed()) + "\"");
        stringBuilder.append(",");

        if (reportLine.isAnswerReviewed())
        {
            stringBuilder.append("\"" + reportLine.getSupervisorName() + "\"");
            stringBuilder.append(",");
            stringBuilder.append("\"" + simpleDateFormat.format(reportLine.getOverridedDate()) + "\"");
        }
        else
        {
            stringBuilder.append("");
            stringBuilder.append(",");
            stringBuilder.append("");

        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}

