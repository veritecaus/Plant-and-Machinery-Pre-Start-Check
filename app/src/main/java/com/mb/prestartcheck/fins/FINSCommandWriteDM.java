package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

/************************************************************
 *
 **********************************************************/
public class FINSCommandWriteDM  extends  FINSCommand {

    private byte memAreaCode;
    private int address;
    private int noItems;

    public  FINSCommandWriteDM(int address, byte[] data, int clientNodeAddr, int sid)
    {
        super(clientNodeAddr, sid);
        this.header.setCommandCode((byte)0x1, (byte)0x2);
        //Write to the DM memory area of the PLC by words.
        this.memAreaCode = FINSUtils.getByte(0x82);
        this.address = address;
        this.noItems =  data.length/2;
        this.commandText = data;

    }

    public byte[] serilize()
    {
        //Address is three bytes.
        //no of items is by bytes.
        ByteBuffer bb = ByteBuffer.allocate(this.header.getLength() + 6 + this.commandText.length);
        bb.put(this.header.serilize());
        bb.put(this.memAreaCode);
        //The beginnng address as a number is represented by three bytes where
        //most significate bytes is first:
        //The second byte -- from left to right -- is shifted to the right by most byte.
        //The PLC assembles the number  left to right.
        byte addrbyte1 = (byte) (address & 0xFF00 >> 8);
        byte addrbyte2 = (byte) (address & 0xFF);
        bb.put(addrbyte1);
        bb.put(addrbyte2);
        bb.put((byte)0x0);
        //no of items -- in words.
        bb.put((byte)0x0);
        bb.put(FINSUtils.getByte(this.noItems));
        bb.put(commandText);
        return bb.array();
    }

    @Override
    public int getLength() {
        return this.header.getLength() + 6 + commandText.length;
    }


}
