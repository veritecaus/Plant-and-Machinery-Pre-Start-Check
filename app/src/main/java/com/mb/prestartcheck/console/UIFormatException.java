package com.mb.prestartcheck.console;


import android.view.View;

public class UIFormatException extends Exception {
    private final View view;

    public View getView() { return this.view;}

    public  UIFormatException(View e, String msg)
    {
        super(msg);
        view = e;
    }


}
