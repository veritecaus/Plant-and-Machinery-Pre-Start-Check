package com.mb.prestartcheck.fins;

import android.util.Log;

import androidx.annotation.Nullable;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;

import java.io.IOException;
import java.nio.ByteBuffer;

/************************************************************
 *  Confine communications with the PLC to one persistent
 *  instance of this class.
 **********************************************************/
public class FINSSession {

    //Keep this connection alive for the life time of the session.
    private  FINSConnection finsConnection;
    //public static  int clientNodeAddress = 0x00;
    public static  int serverNodeAddress = 0x00;
    //public static  int sid = 0x0;

    private   int clientNodeAddress = 0x00;
    private   int sid = 0x0;

    public int getClientNodeAddress() { return clientNodeAddress;}
    public void setClientNodeAddress(int node) {  clientNodeAddress = node;}

    public int getSid() { return sid;}

    public FINSSession(String ip)
    {
        this.finsConnection = new FINSConnection(ip);
        this.sid = FINSUtils.getRandomInt(256);
    }

    public void setIp(String ip)
    {
        if (this.finsConnection != null) this.finsConnection.setIp(ip);
    }

    public void open(int clientNodeAddress) throws IOException, InterruptedException, FINSException
    {

        this.finsConnection.connect();

        //Initialize the session by requesting a client address.
        FINSTCPHeaderNodeAddress finstcpHeaderNodeAddress = new FINSTCPHeaderNodeAddress();
        finstcpHeaderNodeAddress.setClientNodeAddress(clientNodeAddress);

        byte[] payload = finstcpHeaderNodeAddress.serialize();
        this.finsConnection.write(payload,  payload.length, 0);

        //Response from PLC.
        byte[] response = new byte[FINSDefs.MAXIMUM_PACKET_SIZE];
        int read = this.finsConnection.read(response);

        ByteBuffer bb = ByteBuffer.allocate(read);
        bb.put(response, 0, read);

         FINSTCPHeaderNodeAddress  finstcpHeaderNodeAddressResp = new FINSTCPHeaderNodeAddress();

         try {
             finstcpHeaderNodeAddressResp.deserialize(bb);
             this.clientNodeAddress = finstcpHeaderNodeAddressResp.getClientNodeAddress();
             FINSSession.serverNodeAddress = finstcpHeaderNodeAddressResp.getServerNodeAddress();

             AppLog.getInstance().print("*****Client node address is %d .Server address is %d", clientNodeAddress, FINSSession.serverNodeAddress );

         }
         catch(FINSException finsException)
         {
             AppLog.getInstance().print(finsException.getMessage());
             throw finsException;
         }
         catch (Exception ex)
         {
             AppLog.getInstance().print(ex.getMessage());
         }




    }

    public  void close()
    {
        try {
            if (this.finsConnection != null) this.finsConnection.disconnect();

           FINSSession.serverNodeAddress = 0x00;
        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    private  FINSTCPHeaderCommand write(byte[] bytes, int length, int offset) throws IOException
    {
        this.finsConnection.write(bytes, length, offset);
        byte[] response = new byte[FINSDefs.MAXIMUM_PACKET_SIZE];
        int read = this.finsConnection.read(response);

        ByteBuffer bb = ByteBuffer.allocate(read);
        bb.put(response, 0, read);

        FINSTCPHeaderCommand  finstcpHeaderCommand = new FINSTCPHeaderCommand();

        try {
            finstcpHeaderCommand.deserialize(bb);

            return  finstcpHeaderCommand;

        }
        catch(FINSException finsException)
        {
            Log.e(FINSDebug.TAG, finsException.getMessage());
        }
        catch (Exception ex)
        {
            Log.e(FINSDebug.TAG, ex.getMessage());
        }

        return null;
    }

    public FINSConnection getFinsConnection() { return this.finsConnection;}

    public boolean isActive()
    {
        return this.finsConnection != null && this.finsConnection.isConnected() ;
    }

    @Nullable
    public byte[] readMemory(int address,int memArea, int noWords) throws Exception
    {

        FINSFrameRead frame = new FINSFrameRead(address, memArea,  noWords, this.clientNodeAddress, this.sid);
        byte[] bbCommand =frame.serilize();

        this.finsConnection.write(bbCommand, bbCommand.length, 0);

        byte[] response = new byte[FINSDefs.MAXIMUM_PACKET_SIZE];
        int read = this.finsConnection.read(response);
        if (read > 0)
        {

            ByteBuffer bbResponse = ByteBuffer.allocate(read);
            bbResponse.put(response, 0, read);

            frame.deserialize(bbResponse, read);

            return frame.getData();

        }
        else
            AppLog.getInstance().print("PLC did not respond to FINSFrameRead command.");

        return null;
    }

    public boolean writeWRMemoryByByte(int address,  byte[] data) throws Exception
    {
        FINSFrameWriteWR frameWriteWR = new FINSFrameWriteWR(address, data, this.clientNodeAddress, this.sid);
        byte[] bytes = frameWriteWR.serilize();

        FINSTCPHeaderCommand finstcpHeaderCommand = this.write(bytes, bytes.length, 0);

        return finstcpHeaderCommand != null && finstcpHeaderCommand.getErrorCode() == 0x00;
    }

    public boolean writeDMMemoryByByte(int address,  byte[] data) throws Exception
    {
        FINSFrameWriteDM frameWriteDM = new FINSFrameWriteDM(address, data, this.clientNodeAddress, this.sid);
        byte[] bytes = frameWriteDM.serilize();

        FINSTCPHeaderCommand finstcpHeaderCommand = this.write(bytes, bytes.length, 0);

        return finstcpHeaderCommand != null && finstcpHeaderCommand.getErrorCode() == 0x00;
    }


    public boolean writeWRMemoryByBit(int address,  byte[] data, int offset) throws Exception
    {
        byte memA = FINSUtils.getByte(0x31);
        FINSFrameWriteWR frameWriteWR = new FINSFrameWriteWR(address, data, memA, offset, this.clientNodeAddress, this.sid);
        byte[] bytes = frameWriteWR.serilize();

        FINSTCPHeaderCommand finstcpHeaderCommand = this.write(bytes, bytes.length, 0);

        return finstcpHeaderCommand != null && finstcpHeaderCommand.getErrorCode() == 0x00;

    }





}
