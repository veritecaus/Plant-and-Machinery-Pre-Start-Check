package com.mb.prestartcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.fins.FINSDefs;
import com.mb.prestartcheck.fins.FINSSession;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;


public class Machine {

    public  interface MachineInterlockStateDeviceListener
    {
        void onMachineInterlockDeviceStateRead(boolean isClosed);
    }

    public  interface MachineCommsListener {
        void onMachineInitComms(Boolean connected);
    }

    public  interface MachineInterlockDeviceActionListener
    {
        void onMachineInterlockDeviceClosed();
        void onMachineInterlockDeviceOpened();
    }

    public  interface MachineInterlockDeviceMemoryReadListener
    {
        void onMachineBypassReadCompleted(Object[] lines);
    }


    /**
     * Implement this interface to get the "operating hours"
     * value form the PLC ( DM 0).
     */
    public  interface MachineInterlockDeviceOperatingHourListener
    {
        void onMachineOpertingHoursRead(int opHours);
        void onMachineOpertingHoursSet();
    }



    private static Machine instance;

    public String getName() {

        return App.getInstance().getSettings().get(Settings.MACHINE_NAME);
    }


    private FINSSession finsSession ;
    private final int NO_BYPASS_ENTRIES_PER_READ = 20;
    private final int TOTAL_BYPASS_ENTRIES = 1000;
    private final int BYPASS_ENTRIES_START_ADDR = 0x64;



    //public FINSSession getFinsSession() { return this.finsSession;}

    public static Machine getInstance()
    {
        if (instance == null) {
            instance = new Machine();
        }
        return instance;
    }


    public void openInterlockDevice(final MachineInterlockDeviceActionListener listener)
    {
        AppLog.getInstance().print(DateFormaterLog.instance().format(new Date()) + ": Opening interlock device.");

        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Ensure that the app is connected to the PLC.
/*
                    if (Machine.this.finsSession != null && !Machine.this.finsSession.isActive())
                    {
                        if (!reconnectSession())
                            return;
                    }
*/
                    Machine.this.finsSession.writeWRMemoryByBit(100, new byte[] { 0x00}, 0);

                    if (listener != null ) listener.onMachineInterlockDeviceOpened();

                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::openInterlockDevice, %s.",ex.getMessage());
                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();
                }
            }
        });


    }

    public void closeInterlockDevice(final MachineInterlockDeviceActionListener listener)
    {
        AppLog.getInstance().print(DateFormaterLog.instance().format(new Date()) + ": closing the interlock  device.");

        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
/*
                    if (Machine.this.finsSession != null && !Machine.this.finsSession.isActive())
                    {
                        if (!reconnectSession())
                            return;
                    }
*/
                    //Ensure that the app is connected to the PLC.
                    Machine.this.finsSession.writeWRMemoryByBit(0x00, new byte[] { 0x01}, 0);

                    if (listener != null ) listener.onMachineInterlockDeviceClosed();

                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::closeInterlockDevice, %s.",ex.getMessage());
                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }
            }
        });


    }

    public void isClosed(final MachineInterlockStateDeviceListener listener)
    {
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
/*
                    if (Machine.this.finsSession != null && !Machine.this.finsSession.isActive())
                    {
                        if (!reconnectSession())
                            return;
                    }

 */

                    byte[] bytes = finsSession.readMemory(100, FINSDefs.MEMORY_AREA_CODE_WR, 1);


                    boolean isClosed = false;
                    if (bytes != null && bytes.length == 2)
                    {

                        isClosed = (bytes[1] & 1) > 0;

                    }

                    if (listener != null) listener.onMachineInterlockDeviceStateRead(isClosed);

                }

                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::isClosed, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();
                }

            }
        });


    }


    /**
     *  Read an integer value from the "DM" memory addrss at 0x00. This register
     *  contains  (in minutes) the duration that the machine on before the
     *  interlock device is reopened.
     * @param listener A reference to the a class that implements the MachineInterlockDeviceOperatingHourListener
     *                 interface.
     */
    public void readOperatingHours(@NonNull  final MachineInterlockDeviceOperatingHourListener listener)
    {
        //Interact with the PLC on a seperate thread to avoid doing networking on
        //the UI  thread.
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    //Use the read memory FINS command.
                    byte[] bytes = finsSession.readMemory(0x00, FINSDefs.MEMORY_AREA_CODE_DM, 1);

                    //TODO: Check byte ordering.
                    if (bytes != null && bytes.length == 2)
                    {
                        //Using the ByteBuffer class, serialize the data read from the PLC
                        //to get the integer value.
                        byte[] tmpBytes = new byte[] { 0x0, 0x0, bytes[0], bytes[1]};
                        ByteBuffer bb =  ByteBuffer.wrap(tmpBytes);

                        int opertingDuration = bb.getInt();
                        // Notify  listerners of the operating hours value.

                        if (listener != null) listener.onMachineOpertingHoursRead(opertingDuration);

                    }



                }

                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::readOperatingHours, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }

            }
        });

    }

    /**
     *  Set  the integer value at  "DM" memory addrss at 0x00. This register
     *  contains  (in minutes) the duration that the machine on before the
     *  interlock device is reopened.
     * @param listener A reference to the a class that implements the MachineInterlockDeviceOperatingHourListener
     *                 interface.
     * @param timeout Operating hours in minutes.
     *
     */
    public void writeOperatingHours(@NonNull  final MachineInterlockDeviceOperatingHourListener listener,
                                    int timeout)
    {
        //Interact with the PLC on a seperate thread to avoid doing networking on
        //the UI  thread.
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Write timeout to D0.
                    byte msb = (byte)((timeout & 65280)>>8);
                    byte lsb = (byte)(timeout);

                    Machine.this.finsSession.writeDMMemoryByByte(0x00, new byte[] {msb, lsb});

                    //Notify listerner that the value was set in the interlock device.
                    if (listener != null) listener.onMachineOpertingHoursSet();

                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::writeOperatingHours, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }

            }
        });

    }

    public void openIfClosedInterlockDevice(final MachineInterlockDeviceActionListener listener)
    {
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
/*
                    if (Machine.this.finsSession != null && !Machine.this.finsSession.isActive())
                    {
                        if (!reconnectSession())
                            return;
                    }

 */

                    byte[] bytes = finsSession.readMemory(100, FINSDefs.MEMORY_AREA_CODE_WR, 1);


                    boolean isClosed = false;
                    if (bytes != null && bytes.length == 2)
                    {

                        isClosed = (bytes[1] & 1) > 0;
                        if (isClosed) {
                            Machine.this.finsSession.writeWRMemoryByBit(100, new byte[]{0x00}, 0);

                        }

                    }


                    if (listener != null ) listener.onMachineInterlockDeviceOpened();
                }

                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::isClosed, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }

            }
        });


    }

    public void closeIfOpenInterlockDevice(final MachineInterlockDeviceActionListener listener, int timeout)
    {
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
/*
                    if (Machine.this.finsSession != null && !Machine.this.finsSession.isActive())
                    {
                        if (!reconnectSession())
                            return;
                    }

 */

                    byte[] bytes = finsSession.readMemory(100, FINSDefs.MEMORY_AREA_CODE_WR, 1);


                    boolean isClosed = false;
                    if (bytes != null && bytes.length == 2)
                    {

                        isClosed = (bytes[1] & 1) > 0;
                        if (!isClosed) {

                            //Write timeout to D0.
                            byte msb = (byte)((timeout & 65280)>>8);
                            byte lsb = (byte)(timeout);

                            Machine.this.finsSession.writeDMMemoryByByte(0x00, new byte[] {msb, lsb});

                            Machine.this.finsSession.writeWRMemoryByBit(0x00, new byte[] { 0x01}, 0);
                        }

                    }


                    if (listener != null ) listener.onMachineInterlockDeviceClosed();                }

                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::isClosed, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }

            }
        });


    }

    public void initComms(final MachineCommsListener listener, final @NonNull String deviceAddr, final String finsNodeNo)
    {
        if (finsSession == null ) finsSession = new FINSSession(deviceAddr);
        else {
            //When settings has changed.
            finsSession.setIp(deviceAddr);
        }

        finsSession.setClientNodeAddress(Integer.parseInt(finsNodeNo));


        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                boolean connected = false;
                try {
                    String nodeaddr = finsNodeNo;

                    int nodeAddr = Integer.parseInt(nodeaddr);
                    if (nodeAddr == 0) nodeAddr = 0xC8; //use 200 as the client node as default.
                    AppLog.getInstance().print("Initiating comms with %s. Requesting client address %d .",
                            finsSession.getFinsConnection().getIp(),  finsSession.getClientNodeAddress());

                    finsSession.open(nodeAddr);

                    //Top level classes call this method to get a client node address from the PLC,
                    //so auditing of a successful connection at the FINS TCP/IP level is done here.
                    AppLog.getInstance().eventFINCTCPSuccess();
                    connected = true;

                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print(ex.getMessage());

                }

                if (!connected) {
                    finsSession = null;
                    //Audit failed interlock device connection.
                    AppLog.getInstance().eventPLCTCPIPLost();
                }

                if (listener != null) listener.onMachineInitComms(connected);
            }
        });

    }

    public  boolean isSettingValid()
    {
        Settings  settings=App.getInstance().getSettings();
        return settings.contains(Settings.INTERLOCK_DEVICE_ADDRESS) &&
                settings.get(Settings.INTERLOCK_DEVICE_ADDRESS) != null &&
                !settings.get(Settings.INTERLOCK_DEVICE_ADDRESS).isEmpty();
    }

    public  void endComms()
    {
        if (this.finsSession != null) this.finsSession.close();
    }

    @Nullable
    public  void getBypassEntries(MachineInterlockDeviceMemoryReadListener listener)
    {
        AppExecutorService.getInstance().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    ArrayList<ReportLineByPass> lines = new ArrayList<ReportLineByPass>();
                    int noEntriesPerRead = NO_BYPASS_ENTRIES_PER_READ;
                    int totalEntriesRead = 0;
                    int startAddr = BYPASS_ENTRIES_START_ADDR;
                    for(totalEntriesRead = 0; totalEntriesRead < TOTAL_BYPASS_ENTRIES; totalEntriesRead += noEntriesPerRead)
                    {
                        byte[] bytes = Machine.this.finsSession.readMemory(startAddr, FINSDefs.MEMORY_AREA_CODE_DM,
                                10 * noEntriesPerRead);

                        if (bytes != null)
                        {
                            for(int idx = 0; idx < noEntriesPerRead; idx++)
                            {
                                int startPos = idx*20;

                                byte[] bentry = {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
                                                 0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0};
                                for(int jdx = 0; jdx< 20;jdx++) bentry[jdx] = bytes[startPos + jdx];

                                ReportLineByPass reportLineByPass = new ReportLineByPass(bentry);
                                lines.add(reportLineByPass);
                            }

                        }

                        startAddr += noEntriesPerRead *10;

                    }


                    if (listener != null) listener.onMachineBypassReadCompleted(lines.toArray());


                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print("Machine::getBypassEntries, %s.",ex.getMessage());

                    //Disconnect the socket used in the FINS session because it has a broken
                    //pipe. For now, the application reconnects to the PLC at the start
                    //of questioning.
                    finsSession.close();

                }
            }
        });


    }

/*
    private boolean reconnectSession()
    {
        String deviceAddr  = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS);
        String  finsNodeNo = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS);

        if (finsSession == null ) finsSession = new FINSSession(deviceAddr);
        else {
            //When settings has changed.
            finsSession.setIp(deviceAddr);
        }

        finsSession.setClientNodeAddress(Integer.parseInt(finsNodeNo));

        boolean connected = false;
        try {
            String nodeaddr = finsNodeNo;

            int nodeAddr = Integer.parseInt(nodeaddr);
            if (nodeAddr == 0) nodeAddr = 0xC8; //use 200 as the client node as default.
            AppLog.getInstance().print("Reconnecting comms with %s. Requesting client address %d .",
            finsSession.getFinsConnection().getIp(), finsSession.getClientNodeAddress());

            finsSession.open(nodeAddr);

            connected = true;

        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());

        }

        if (!connected) {
            finsSession = null;
            AppLog.getInstance().print("Could not connect to interlock device on %s.",  deviceAddr);
        }

        return connected;
    }
*/

    public  boolean hasComms()
    {
        //Audit the disconnected PLC connection here because  methods outside this class
        //must call this method to check the state of the PLC connection.

        boolean hasConnection = this.finsSession != null && this.finsSession.isActive();

        //Add application log entry
        if (!hasConnection) AppLog.getInstance().eventPLCTCPIPLost();

        return hasConnection;
    }

    public  int getCurrentFINNodeNumber ()
    {
        return this.finsSession != null && this.finsSession.isActive() ? this.finsSession.getClientNodeAddress() : 0;
    }

     public  String getCurrentInterlockDeviceAddress()
     {
         return this.finsSession != null && this.finsSession.getFinsConnection() !=null ? this.finsSession.getFinsConnection().getIp() :
                 "";
     }


}
