package com.mb.prestartcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormaterEntry extends DateFormat {
    private final String format = "d/M/y";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

    @NonNull
    @Override
    public StringBuffer format(@NonNull Date date, @NonNull  StringBuffer toAppendTo, @NonNull  FieldPosition fieldPosition) {
        return simpleDateFormat.format(date, toAppendTo, fieldPosition);
    }

    @Nullable
    @Override
    public Date parse(@NonNull  String source, @NonNull  ParsePosition pos) {
        return simpleDateFormat.parse(source, pos);
    }
}
