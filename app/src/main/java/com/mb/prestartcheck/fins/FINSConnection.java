package com.mb.prestartcheck.fins;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/************************************************************
 *  Manage the underlying TCP/IP connection to the PLC.
 **********************************************************/
public class FINSConnection {

    private Socket socket;
    private InetSocketAddress socketAddress;
    private String ip;
    private int soTimeOut = 10; //in seconds
    public FINSConnection(String addressIp)
    {

        this.ip  = addressIp;
        this.socketAddress = new InetSocketAddress(addressIp, FINSDefs.TCPIP_PORT);
    }

    public FINSConnection(String addressIp, int timeout)
    {
        this(addressIp);
        this.soTimeOut = timeout;
    }

    public String getIp() { return this.ip;}

    public void setIp(String ip) {
        this.ip = ip;
        socketAddress = new InetSocketAddress(this.ip, FINSDefs.TCPIP_PORT);
    }

    public int getSoTimeOut() { return this.soTimeOut;}
    public void setSoTimeOut(int seconds) { this.soTimeOut = seconds;}

    public void connect() throws IOException
    {
        //Disconnect and reconnect.
        disconnect();
        this.socket = new Socket();
        int timeout = 1000 * this.soTimeOut;
        this.socket.setSoTimeout(timeout);

        //Catch the exception for auditing purposes then raise it again.
        try {
            this.socket.connect(this.socketAddress, timeout);
            AppLog.getInstance().eventPLCTCPIPSuccess();
        }
        catch(IOException ioException)
        {
            AppLog.getInstance().eventPLCTCPIPLost();
            throw ioException;
        }


    }

    public  void disconnect() throws IOException
    {
        if (this.socket != null)
        {
            this.socket.close();
            this.socket = null;
            AppLog.getInstance().eventPLCTCPIPDisconnet();
        }
    }

    public void write(byte[] buffer, int length, int offset) throws IOException
    {
        OutputStream outputStream = this.socket.getOutputStream();
        outputStream.write(buffer, offset, length);
    }

    public int read(byte[] response) throws IOException
    {

        int total =0;
        int bytes= 0;
        byte[] dest = new byte[0];

        InputStream inputStream = this.socket.getInputStream();

        do {
            byte[] read =  new byte[FINSDefs.MAXIMUM_PACKET_SIZE];
            //Wait for the PLC to respond

            bytes = inputStream.read(read, 0, FINSDefs.MAXIMUM_PACKET_SIZE);

            if (bytes > 0) {


                total += bytes;
                //Reallocate final buffer.
                if (total > dest.length) {
                    byte[] tmp = new byte[total];
                    //Copy existing bytes to new buffer.
                    for (int i = 0; i < dest.length; i++) tmp[i] = dest[i];
                    //Copy the new read bytes to the nw buffer
                    for (int i = 0; i < bytes; i++) tmp[dest.length + i] = read[i];

                    dest = tmp;
                }
            }

        }
        while(bytes >= FINSDefs.MAXIMUM_PACKET_SIZE );

        //Copy what the PLC sent into the response buffer.

        for(int i = 0; i < total; i++) response[i] = dest[i];

        return total;
    }

    public boolean isConnected()
    {
         return socket != null && !this.socket.isClosed() && socket.isConnected();
    }

}
