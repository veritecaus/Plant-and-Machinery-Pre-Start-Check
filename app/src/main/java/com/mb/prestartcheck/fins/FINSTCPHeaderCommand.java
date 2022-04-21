package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public class FINSTCPHeaderCommand  extends FINSTCPHeader{

    public FINSTCPHeaderCommand()
    {
        super();
        this.command = 0x2;
    }

    @Override
    public byte[] serialize() {
        //Set the length of the command frame -- command and error will always take 8 bytes.
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putInt(0,this.header );
        bb.putInt(4,this.length );
        bb.putInt(8,this.command );
        bb.putInt(12,this.errorCode );

        return bb.array();
    }

    public void deserialize(ByteBuffer bb) throws FINSException
    {
        super.deserialize(bb);
        validate();
    }

}
