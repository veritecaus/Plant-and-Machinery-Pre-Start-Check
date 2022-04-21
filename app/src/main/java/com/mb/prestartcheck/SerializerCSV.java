package com.mb.prestartcheck;

import java.text.SimpleDateFormat;

public class SerializerCSV {
    private static final String dateFormat = "y-M-d";
    private static final String timeFormat = "h:m";
    private static final String dateTimeFormat = "y-M-d h:m:s";

    public static String build (Response response, User user)
    {
        String machineName = Machine.getInstance().getName();
        SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat);
        SimpleDateFormat sdfTime   = new SimpleDateFormat(timeFormat);
        SimpleDateFormat sdfDateTime   = new SimpleDateFormat(dateTimeFormat);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(machineName);
        stringBuilder.append(",");
        stringBuilder.append(user.getFirstName());
        stringBuilder.append(",");
        stringBuilder.append(user.getLastName());
        stringBuilder.append(",");
        stringBuilder.append(sdfDate.format(response.getCreatedDateTime()));
        stringBuilder.append(",");
        stringBuilder.append(sdfTime.format(response.getCreatedDateTime()));
        stringBuilder.append(",");
        stringBuilder.append(response.getSection().getId());
        stringBuilder.append(",");
        stringBuilder.append(response.getQuestion().getSequence());
        stringBuilder.append(",");
        stringBuilder.append(response.getQuestion().getNumber());
        stringBuilder.append(",");
        stringBuilder.append(response.getQuestion().isNegativePositive?"true" : "false");
        stringBuilder.append(",");
        stringBuilder.append(response.getQuestion().getTitle());
        stringBuilder.append(",");
        stringBuilder.append(response.getQuestion().getTypeString());
        stringBuilder.append(",");
        stringBuilder.append(response.getExpectedResponse());
        stringBuilder.append(",");
        stringBuilder.append(response.getOperatorResponse());
        stringBuilder.append(",");
        stringBuilder.append(response.getMachineLocked()? "true" : "false");
        stringBuilder.append(",");
        stringBuilder.append(response.getAnswerReviewed()? "true" : "false");
        stringBuilder.append(",");
        stringBuilder.append(response.getClearBy());
        stringBuilder.append(",");
        stringBuilder.append(response.getClearBy() == null ? "" : sdfDateTime.format(response.getClearBy()));
        return stringBuilder.toString();

    }
}
