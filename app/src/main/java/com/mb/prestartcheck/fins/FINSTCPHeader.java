package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public abstract class FINSTCPHeader {

    protected int header = 0x46494E53; //Four bytes header that is ascii equivalent to 'FINS'
    protected int length = 0x00; // From command onwards.
    protected int command = 0x00;
    protected int errorCode = 0x00;

    public void setFrameLength(int value) { this.length = value;}
    public  int getFrameLength() { return this.length;}

    public abstract byte[] serialize();

    public void deserialize(ByteBuffer bb) throws FINSException
    {
        this.header = bb.getInt(0);
        this.length  = bb.getInt(4);
        this.command  = bb.getInt(8);
        this.errorCode  = bb.getInt(12);
    }

    protected void validate() throws FINSException
    {
        if (this.errorCode == 0x0) return ;

        if (this.errorCode == 0x1) throw new FINSException("The header is not ‘FINS’ (ASCII code).");

        if (this.errorCode == 0x2) throw new FINSException("The data length is too long.");

        if (this.errorCode == 0x3) throw new FINSException("The command is not supported.");


    }

    //Get length of the header starting at the command.
    public int getLength() { return 8;}
    public void setLength(int len) { this.length = len;}

    public  int getCommand()  { return command;}
    public  int getErrorCode()  { return errorCode;}




}
