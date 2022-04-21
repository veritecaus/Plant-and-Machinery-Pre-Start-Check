package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableResponse;

import java.util.Date;

public  class Response {
    private final  Question question;
    private final  Section section;

    private Date createdDateTime; //When the response was answered.
    private Date updatedDateTime; // When the response was committed to the database.
    private String expectedResponse;
    private String operatorResponse;
    private boolean machineLocked;
    private String clearBy;
    private Date clearAt;
    private long id;
    private boolean answerReviewed;
    private boolean isNegative;

    public long getId() {  return id;  }
    public void setId(long val)  {  this.id = val; }

    public  Question getQuestion() {return question;}
    public  Section getSection() {return section;}

    public  Date getCreatedDateTime() {  return createdDateTime;}
    public Date getUpdatedDateTime() { return updatedDateTime;}

    public String getExpectedResponse() { return this.expectedResponse;}
    public void setExpectedResponse(String val) { this.expectedResponse = val;}

    public String getOperatorResponse() { return this.operatorResponse; }
    public void setOperatorResponse(String val) { this.operatorResponse = val;}

    public boolean getMachineLocked() { return this.machineLocked;}
    public void setMachineLocked(boolean val) { this.machineLocked = val;}

    public String getClearBy() { return this.clearBy;}
    public void setClearBy(String val) { this.clearBy = val;}

    public Date getClearAt() { return this.clearAt;}
    public void setClearAt(Date val) { this.clearAt = val;}

    public boolean getAnswerReviewed() { return this.answerReviewed;}
    public void setAnswerReviewed(boolean val) { this.answerReviewed = val;}

    public void setCreatedDateTime(Date val) { this.createdDateTime = val;}
    public void setUpdatedDateTime(Date val) { this.updatedDateTime = val;}

    public boolean getIsNegative() { return this.isNegative;}
    public void setIsNegative(boolean val) { this.isNegative = val;}

    public  void setReviewed(boolean accepted, Supervisor supervisor)
    {
        this.answerReviewed = accepted;
        this.clearBy = accepted ? supervisor.getFullName() : "";
        this.clearAt = accepted ? new Date() : null;
    }

    public void clearReviewed()
    {
        this.answerReviewed = false;
        this.clearBy = "";
        this.clearAt = null;
    }

    public boolean isReviewed()
    {
        return this.answerReviewed && !this.clearBy.isEmpty() && this.clearAt != null;
    }

    public  Response(Question q, Section s)
    {
        this.question = q;
        this.section = s;
    }

    public TableResponse toTableResponse(User o, Machine m)
    {
        Date now = new Date();

        TableResponse tableResponse = new TableResponse();
        tableResponse.id = this.id  ;
        tableResponse.machine = m.getName();
        tableResponse.first_name = o.getFirstName();
        tableResponse.last_name = o.getLastName();
        tableResponse.date = createdDateTime == null ? now.getTime() : createdDateTime.getTime();
        tableResponse.time = updatedDateTime == null ? now.getTime() : updatedDateTime.getTime();
        tableResponse.section = this.section.getId();
        tableResponse.question_sequence = this.question.getSequence();
        tableResponse.question_id = this.question.getId();
        tableResponse.question_title = this.isNegative ? this.question.getTitleAlternative() :  this.question.getTitle();
        //TODO: Record if the negative question was asked.
        tableResponse.pos_neg = this.isNegative ? 1 : 0;
        tableResponse.question_text = this.isNegative ? this.question.getTitleAlternative() :  this.question.getTitle();
        tableResponse.response_type = this.question.getTypeString();
        tableResponse.expected_response  = this.question == null ? "" :(this.isNegative ?  this.question.getExpectedAnswerNeg() :
                this.question.getExpectedAnswer());
        tableResponse.operator_response  = this.operatorResponse;
        tableResponse.machine_unlocked  = this.machineLocked ? 1 : 0;
        tableResponse.answer_reviewed = this.answerReviewed ? 1 : 0;
        tableResponse.cleared_by  = this.clearBy;
        tableResponse.cleared_at = (this.clearAt == null ? 0 : this.clearAt.getTime());
        tableResponse.updated_datetime = this.getUpdatedDateTime() == null ? now.getTime() :  this.getUpdatedDateTime().getTime();
        tableResponse.created_datetime = this.getCreatedDateTime() == null ? now.getTime() : this.getCreatedDateTime().getTime();
        tableResponse.deleted = 0;
        tableResponse.session_uuid =  Session.getInstance().getUuid();


        return tableResponse;

    }




}
