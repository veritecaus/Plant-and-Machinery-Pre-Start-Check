package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public class FINSFrameWriteDM  extends  FINSFrame {

    public  FINSFrameWriteDM(int address, byte[] data, int clientNodeAddr, int sid)
    {
        super();
        this.tcpipHeader = new FINSTCPHeaderCommand();

        this.finsCommand = new FINSCommandWriteDM(address, data, clientNodeAddr,  sid);

    }

    @Override
    protected byte[] serilize() {

        //Since we know the payload, proceed to calculate the length of the frame:
        //defined as the number of bytes from the command byte.
        int length = this.tcpipHeader.getLength()  + this.finsCommand.getLength();
        this.tcpipHeader.setFrameLength(length);

        ByteBuffer bb = ByteBuffer.allocate(length + 8); // First 8 bytes for the 'FINS' word and length .
        bb.put(this.tcpipHeader.serialize());
        bb.put(this.finsCommand.serilize());
        return bb.array();
    }



}
