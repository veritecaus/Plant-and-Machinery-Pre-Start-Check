package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

public class FINSTCPHeaderNodeAddress extends  FINSTCPHeader {

    private int clientNodeAddress = 0x00 ; //Let the PLC assign a node address in the range of 0 - 254.
    private int serverNodeAddress = 0x00;


    public int getClientNodeAddress() { return clientNodeAddress;}
    public void setClientNodeAddress(int val) { this.clientNodeAddress =val;}
    public int getServerNodeAddress() { return serverNodeAddress;}

    public FINSTCPHeaderNodeAddress()
    {
        super();
        this.command = 0x00;
    }

    @Override
    public byte[] serialize() {
        //Set the length of the command frame -- command and error will always take 8 bytes.
        //The client node address is 4 bytes  , so 12 bytes total
        length = 0xC;
        byte[] bytes = new byte[8 + this.length];
        ByteBuffer bb = ByteBuffer.allocate(8 + this.length);
        bb.putInt(0,this.header );
        bb.putInt(4,this.length );
        bb.putInt(8,this.command );
        bb.putInt(12,this.errorCode );
        bb.putInt(16,this.clientNodeAddress );

        return bb.array();

    }

    public void deserialize(ByteBuffer bb) throws FINSException
    {
        super.deserialize(bb);

        if (this.length < 0x10) throw new FINSException("FINS TCP/IP Header length field is less than expected.");

        this.clientNodeAddress = bb.getInt(16);
        this.serverNodeAddress = bb.getInt(20);

        validate();

    }

    protected void validate() throws FINSException
    {
        if (this.errorCode == 0x0) return ;

        if (this.errorCode == 0x1) throw new FINSException("The header is not ‘FINS’ (ASCII code).");

        if (this.errorCode == 0x2) throw new FINSException("The data length is too long.");

        if (this.errorCode == 0x3) throw new FINSException("The command is not supported.");

        if (this.errorCode == 0x20) throw new FINSException("All connections are in use.");

        if (this.errorCode == 0x21) throw new FINSException("The specified node is already connected.");

        if (this.errorCode == 0x22) throw new FINSException("Attempt to access a protected node from an unspecified IP address.");

        if (this.errorCode == 0x23) throw new FINSException("The client FINS node address is out of range.");

        if (this.errorCode == 0x24) throw new FINSException("The same FINS node address is being used by the client and server.");

        if (this.errorCode == 0x25) throw new FINSException("All the node addresses available for allocation have been used.");


    }

    public int getLength() { return 12;}
}
