package com.mb.prestartcheck.fins;


import java.nio.ByteBuffer;

/************************************************************
 *
 **********************************************************/
public class FINSCommandWriteWR extends  FINSCommand {
    private byte memAreaCode = FINSUtils.getByte(0xB1);
    private int address;
    private int noItems;
    private int offset;

    public  FINSCommandWriteWR(int address, byte[] data, int clientNodeAddr, int sid)
    {
        super(clientNodeAddr, sid);
        this.header.setCommandCode((byte)0x1, (byte)0x2);
        //Write to the WR memory area of the PLC by words.
        this.memAreaCode = memAreaCode;
        this.memAreaCode = FINSUtils.getByte(0xB1);
        this.address = address;
        this.noItems = data.length/2;
        this.commandText = data;

    }

    public  FINSCommandWriteWR(int address, byte[] data, byte memAreaCode, int offset, int clientNodeAddr, int sid)
    {
        this(address, data ,clientNodeAddr,sid);
        this.memAreaCode = memAreaCode;
        this.offset = offset;

        this.noItems = memAreaCode == FINSUtils.getByte(0xB1) ? data.length/2 : data.length;
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
        byte addrbyte1 = (byte) ((address & 65280) >> 8);
        byte addrbyte2 = (byte) (address & 255);
        bb.put(addrbyte1);
        bb.put(addrbyte2);
        if (this.memAreaCode == FINSUtils.getByte(0xB1)) {
            bb.put((byte) 0x0); // This is the offset when writting to an address using the bit data type.
        }
        else {
            // This is the offset when writing to an address in bits.
            bb.put(FINSUtils.getByte(offset));
        }

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
