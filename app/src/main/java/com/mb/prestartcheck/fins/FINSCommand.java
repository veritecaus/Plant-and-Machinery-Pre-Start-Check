package com.mb.prestartcheck.fins;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class FINSCommand {

    //TODO: Later, this needs to be split into header and command payload.
    FINSHeader header;
    protected  byte[] commandText;
    protected  byte MRES = 0x0;
    protected  byte SRES = 0x0;

    public  FINSCommand(int clientNodeAddr, int sid)
    {
        header = new FINSHeader(clientNodeAddr,sid);
    }
    public abstract  int getLength();

    abstract public byte[] serilize();
    
    public void deserilize(ByteBuffer bb, int totalsize) throws FINSException
    {
        this.header.deserialize(bb);

        //Skip command codes
        //Response codes.
        MRES = bb.get(12);
        SRES = bb.get(13);
        int datalength = totalsize - 14;
        if (datalength > 0) {
            //TODO: Handle 'FINS' errors.
            commandText = new byte[datalength];
            int idxBuffer = 0;
            for(int idx = 14 ; idx < totalsize; idx++) {
                commandText[idxBuffer] = bb.get(idx);
                idxBuffer++;
            }


        }

        validate();
    }

    protected void validate() throws FINSException
    {
        if (this.MRES == 0x0 && this.SRES == 0x0) return;

         throw new FINSException(String.format("Response code: MSRES = %x , SRES = %x", this.MRES, this.SRES));

    }

}
