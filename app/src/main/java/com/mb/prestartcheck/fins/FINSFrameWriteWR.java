package com.mb.prestartcheck.fins;

import com.mb.prestartcheck.AppLog;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FINSFrameWriteWR  extends  FINSFrame {

    public  FINSFrameWriteWR(int address, byte[] data,int clientNodeAddr, int sid)
    {
        super();
        this.tcpipHeader = new FINSTCPHeaderCommand();
        this.finsCommand = new FINSCommandWriteWR(address, data, clientNodeAddr, sid);

    }

    public  FINSFrameWriteWR(int address, byte[] data, byte memoryAreaCode, int offset, int clientNodeAddr, int sid)
    {
        super();
        this.tcpipHeader = new FINSTCPHeaderCommand();
        this.finsCommand = new FINSCommandWriteWR(address, data, memoryAreaCode, offset, clientNodeAddr, sid);

    }

    @Override
    protected byte[] serilize() {

        //Since we know the payload, proceed to calculate the length of the frame:
        //defined as the number of bytes from the command byte.
        int length = this.tcpipHeader.getLength() + this.finsCommand.getLength();
        this.tcpipHeader.setFrameLength(length);

        ByteBuffer bb = ByteBuffer.allocate(length + 8); // First 8 bytes for the 'FINS' word and length .
        bb.put(this.tcpipHeader.serialize());
        bb.put(this.finsCommand.serilize());
        return bb.array();
    }

    public  FINSTCPHeaderCommand send(FINSConnection connection) throws IOException, FINSException
    {

        byte[] bytes = serilize();
        connection.write(bytes, bytes.length, 0);

        //Response from PLC.
        byte[] response = new byte[FINSDefs.MAXIMUM_PACKET_SIZE];
        int read = connection.read(response);
        FINSTCPHeaderCommand commandResp  = new FINSTCPHeaderCommand();

        if (read >  0)
        {
            ByteBuffer bb = ByteBuffer.allocate(read);
            bb.put(response, 0, read);


            commandResp.deserialize(bb);

            return commandResp;

        }
        else
            AppLog.getInstance().print("PLC did not respond to FINSFrameWriteWR.");

        return commandResp;
    }
}
