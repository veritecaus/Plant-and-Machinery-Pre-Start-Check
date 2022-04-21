package com.mb.prestartcheck;

import androidx.annotation.Nullable;

public class ProxyInterlockDevice {

    //Store the last reading of the operating hours value in the PLC
    private static int lastReadingOpHours;

    public static int getLastReadingOpHours() { return lastReadingOpHours;}

    public interface  ProxyInterlockDeviceListener
    {
        void  proxyInterlockDeviceListeneronCommsInit(boolean isConnected);
    }

    public static void connectAndInitializeInterlockDevice(String addr, String finsNode)
    {
        Machine.getInstance().endComms();
        Machine.getInstance().initComms(new Machine.MachineCommsListener() {
            @Override
            public void onMachineInitComms(Boolean connected) {
                if (!connected) return;

                Machine.getInstance().openIfClosedInterlockDevice(new Machine.MachineInterlockDeviceActionListener() {
                    @Override
                    public void onMachineInterlockDeviceClosed() {

                    }

                    @Override
                    public void onMachineInterlockDeviceOpened() {
                        AppLog.getInstance().print("Session::connectAndInitializeInterlockDevice opened the interlock device.");
                    }
                });
            }
        }, addr, finsNode);

    }
    public static void connect(String addr, String finsNode, ProxyInterlockDeviceListener listener)
    {
        Machine.getInstance().endComms();
        Machine.getInstance().initComms(new Machine.MachineCommsListener() {
            @Override
            public void onMachineInitComms(Boolean connected) {
                if (listener !=null) listener.proxyInterlockDeviceListeneronCommsInit(connected);



            }
        }, addr, finsNode);

    }

    /**
     * If the connection to the interlock device is broken, then go through
     * the process of reconnecting it :
     * recreate the TCP/IP socket and initialize the FINS/TCP layer.
     */
    public static void reconnect(String addr, String finsNode, ProxyInterlockDeviceListener listener)
    {
        //Do nothing if the FINS/TCPIP connection is intact.
        if (Machine.getInstance().hasComms())
        {
            AppLog.getInstance().print("ProxyInterlockDevice::reconnect was attempted on an opened connection to the interlock device. Nothing was done.");
            return;
        }


        //This is the same as the "connect" method.
        Machine.getInstance().endComms();
        Machine.getInstance().initComms(new Machine.MachineCommsListener() {
            @Override
            public void onMachineInitComms(Boolean connected) {
                if (listener !=null) listener.proxyInterlockDeviceListeneronCommsInit(connected);

                //Log this event
                AppLog.getInstance().print("ProxyInterlockDevice::reconnect sucessful.");

            }
        }, addr, finsNode);


    }

    public static void openInterlockDevice()
    {
        if (!Machine.getInstance().hasComms()) return;

        Machine.getInstance().openIfClosedInterlockDevice(new Machine.MachineInterlockDeviceActionListener() {
            @Override
            public void onMachineInterlockDeviceClosed() {

            }

            @Override
            public void onMachineInterlockDeviceOpened() {
                AppLog.getInstance().print("Session::openInterlockDevice opened the interlock device.");
            }
        });
    }

    public  static void closeInterlockDevice(int timeout)
    {
        if (!Machine.getInstance().hasComms())  return;

        Machine.getInstance().closeIfOpenInterlockDevice(new Machine.MachineInterlockDeviceActionListener() {
            @Override
            public void onMachineInterlockDeviceClosed() {
                AppLog.getInstance().print("Session::closeInterlockDevice closed the interlock device.");
            }

            @Override
            public void onMachineInterlockDeviceOpened() {

            }
        }, timeout);

    }


    /**
     * Get the operating hours value from the PLC.
     * @param onComplete Call this runnable after reading the value inside the PLC.
     */
    public static void  readOperatingHours(@Nullable Runnable onComplete)
    {
        //Check to see if the interlock is connected.
        if (!Machine.getInstance().hasComms())
        {
            AppLog.getInstance().print("Session::readOperatingHours attempted . NO Comms.");
            return;
        }

        Machine.getInstance().readOperatingHours(new Machine.MachineInterlockDeviceOperatingHourListener() {
            @Override
            public void onMachineOpertingHoursRead(int opHours) {
                //Save the read value.
                lastReadingOpHours = opHours;
                //Pass the operting hours value back to caller
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onMachineOpertingHoursSet() {
                //Ellided.
            }
        });


    }

    /**
     * Set the operating hours value in the PLC and notify the caller.
     * @param onComplete Optionally call a Runnable after the value was set.
     */
    public static void writeOperatingHours(int timeout , @Nullable Runnable onComplete)
    {
        //Check to see if the interlock is connected.
        if (!Machine.getInstance().hasComms())
        {
            AppLog.getInstance().print("Session::writeOperatingHours attempted . NO Comms.");
            return;
        }


        Machine.getInstance().writeOperatingHours( new Machine.MachineInterlockDeviceOperatingHourListener() {
            @Override
            public void onMachineOpertingHoursRead(int opHours) {
                //Ellided.
            }

            @Override
            public void onMachineOpertingHoursSet() {
                //Pass the operting hours value back to caller
                if (onComplete != null) onComplete.run();
            }
        }, timeout);

    }

}
