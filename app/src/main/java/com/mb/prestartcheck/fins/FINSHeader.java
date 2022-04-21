package com.mb.prestartcheck.fins;

import java.nio.ByteBuffer;

/************************************************************
 *  FINS header frame -- not to be confused with FINS TCP/IP
 *  header frame.
 **********************************************************/
public  class FINSHeader {
    protected byte ICF = -0x80;
    protected byte RSV = 0x0; /* Used by the PLC */
    protected byte GCT = 0x7; /* Gate way count */
    protected byte DNA = 0x0;  /*Destination network address: local network*/
    protected byte DA1  = 0x0; /*Destination node address.Obtained from node address request*/
    protected byte DA2 = 0x0; /* Destination unit address. 0 = CPU unit.*/
    protected byte SNA = 0x0; /*Source network address . 0 for Local.*/
    protected byte SA1 = 0x0; /*Source node address.*/
    protected byte SA2 = 0x0; /*Source unit address. 0 for CPU  unit.*/
    protected byte SID =0x0; /* Service ID*/
    protected byte[] commandCode = {0x0, 0x0};

    public FINSHeader(int clientNodeAddress, int sid)
    {
        this.SA1 = FINSUtils.getByte(clientNodeAddress);//FINSUtils.getByte(FINSSession.serverNodeAddress);
        this.DA1 = FINSUtils.getByte(FINSSession.serverNodeAddress);
        //this.DA1 =0x00;
        this.SID = FINSUtils.getByte(sid);
    }

    protected  byte[] serilize()
    {
        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.put(ICF);
        bb.put(RSV);
        bb.put(GCT);
        bb.put(DNA);
        bb.put(DA1);
        bb.put(DA2);
        bb.put(SNA);
        bb.put(SA1);
        bb.put(SA2);
        bb.put(SID);
        bb.put(commandCode);

        return bb.array();
    }

    public void deserialize(ByteBuffer bb)
    {
        this.ICF = bb.get(0);
        this.RSV = bb.get(1);
        this.GCT = bb.get(2);
        this.DNA = bb.get(3);
        this.DA1 = bb.get(4);
        this.DA2 = bb.get(5);
        this.SNA = bb.get(6);
        this.SA1 = bb.get(7);
        this.SA2 = bb.get(8);
        this.SID = bb.get(9);

        this.commandCode = new byte[]{ bb.get(10) , bb.get(11)};

    }


    public  void setCommandCode(byte hb, byte lb) {
        this.commandCode = new byte[] { hb, lb};
    }

    public  int getLength() {  return 12;}
}
