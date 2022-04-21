package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public abstract class FINSFrame {
    protected FINSTCPHeader tcpipHeader;
    protected FINSCommand finsCommand;

    protected abstract  byte[] serilize();

    protected  void  deserialize(ByteBuffer bb, int totalsize) throws  IllegalArgumentException, FINSException
    {
        if (this.tcpipHeader == null ) throw new IllegalArgumentException("FINSFrame::deserialize :  tcpipheader was null .");

        if (this.finsCommand == null) throw new IllegalArgumentException("FINSFrame::deserialize :  this.finscommand was null.");

        this.tcpipHeader.deserialize(bb);
        //Detach the FINS payload.
        byte[] payload = new byte[this.tcpipHeader.getFrameLength() - 8];
        int pidx = 0;
        for(int idx = 16; idx < totalsize; idx++) {
            payload[pidx] = bb.get(idx);
            pidx++;
        }

        ByteBuffer bbPayload =ByteBuffer.allocate(payload.length);
        bbPayload.put(payload);
        this.finsCommand.deserilize(bbPayload, payload.length);
    }

}
