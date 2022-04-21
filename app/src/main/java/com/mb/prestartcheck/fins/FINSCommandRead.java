package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public class FINSCommandRead extends  FINSCommand {

    private byte memAreaCode;
    private int address;
    private int noWords;

    public FINSCommandRead(int address, int areaCode, int noWords,int clientNodeAddr, int sid)
    {
        super(clientNodeAddr, sid);
        this.header.setCommandCode((byte)0x1, (byte)0x1);
        //Write to the DM memory area of the PLC by words.
        this.memAreaCode = FINSUtils.getByte(areaCode);
        this.address = address;
        this.noWords =  noWords;

    }

    public byte[] serilize()
    {
        //Address is three bytes.
        //no of items is by bytes.
        ByteBuffer bb = ByteBuffer.allocate(this.header.getLength() + 6 );
        bb.put(this.header.serilize());
        bb.put(this.memAreaCode);
        //The beginnng address as a number is represented by three bytes where
        //most significate bytes is first:
        //The second byte -- from left to right -- is shifted to the right by most byte.
        //The PLC assembles the number  left to right.
        ByteBuffer bbaddr = ByteBuffer.allocate(4);
        bbaddr.putInt(this.address);

        bb.put(bbaddr.get(2));
        bb.put(bbaddr.get(3));
        bb.put((byte)0x0);
        //no of items -- in words.

        ByteBuffer bbtmp = ByteBuffer.allocate(4);
        bbtmp.putInt(this.noWords);
        byte b1  = bbtmp.get(0);
        byte b2  = bbtmp.get(1);
        byte b3  = bbtmp.get(2);
        byte b4  = bbtmp.get(3);

        bb.put(b3);
        bb.put(b4);

        return bb.array();
    }

    @Override
    public int getLength() {
        return this.header.getLength() + 6 ;
    }


}
