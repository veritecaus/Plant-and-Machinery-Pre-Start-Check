package com.mb.prestartcheck.fins;

/************************************************************
 *  Store the 'FINS' frame  that caused a protocol error.
 *
 **********************************************************/
public class FINSException extends  Exception{

    private String message;

    public  String  getMessage()  {  return this.message; }

    public  FINSException(String msg)
    {
        this.message = msg;
    }

}
