package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public class FINSFrameRead extends  FINSFrame {

    public FINSFrameRead(int address, int memArea, int noWords, int clientNodeAddr, int sid)
    {
        super();
        this.tcpipHeader = new FINSTCPHeaderCommand();
        this.finsCommand = new FINSCommandRead(address, memArea, noWords, clientNodeAddr, sid);

    }

    @Override
    protected byte[] serilize() {

        //Since we know the payload, proceed to calculate the length of the frame:
        //defined as the number of bytes from the command byte.
        int lengthCommandOnwards = this.tcpipHeader.getLength() + this.finsCommand.getLength();
        this.tcpipHeader.setFrameLength(lengthCommandOnwards);

        //this.tcpipHeader.getLength() = 8 , excludes command and error code.
        //Total packet size = this.tcpipHeader.getLength() + length + error code + FINS payload length
        ByteBuffer bb = ByteBuffer.allocate(lengthCommandOnwards + 8); // First 8 bytes for the 'FINS' word and length .
        bb.put(this.tcpipHeader.serialize());
        bb.put(this.finsCommand.serilize());
        return bb.array();
    }

    public void deserialize(ByteBuffer bb, int totalSize) throws  IllegalArgumentException, FINSException
    {
        super.deserialize(bb,totalSize );

    }

    public byte[] getData() { return this.finsCommand.commandText;}


}
