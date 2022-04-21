package com.mb.prestartcheck;

import androidx.annotation.Nullable;

import java.util.Date;

public class ParameterReport {
    private String firstName;
    private String lastName;
    private boolean getAll;

    private Date startDate;
    private Date endDate;

    public boolean isGetAll() { return this.getAll;}
    public String getFirstName() { return this.firstName;}
    public String getLastName() { return this.lastName;}
    public Date getStartDate() { return this.startDate;}
    public Date getEndDate() { return this.endDate;}

    public  ParameterReport(String firstName, String lastName, boolean getAll,
                            @Nullable Date sDate,@Nullable Date eDate)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.getAll = getAll;
        this.startDate = sDate;
        this.endDate = eDate;
    }


}
