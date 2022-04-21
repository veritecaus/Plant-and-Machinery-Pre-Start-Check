package com.mb.prestartcheck.console;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.ReportLineByPass;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.WorkerEmailBypass;
import com.mb.prestartcheck.WorkerHeartbeat;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.FINSSession;
import com.mb.prestartcheck.fins.ResultEnum;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentInterlockDevice extends Fragment implements Machine.MachineInterlockDeviceActionListener,
        Machine.MachineInterlockStateDeviceListener, Machine.MachineCommsListener,
        Machine.MachineInterlockDeviceMemoryReadListener, View.OnClickListener {

    private ViewModelFragmentInterlockDevice viewModel;
    private View rootView;
    private Button btnOpen;
    private Button btnClose;
    private Button btnGetByPassEntires;
    private Button btnConnect;
    private Button btnDisconnect;
    private Button buttonRead;
    private Switch swState;
    private Button buttonSendHeartBeat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_interlock_device, container, false);
        this.viewModel =  new ViewModelFragmentInterlockDevice(getActivity().getApplication());

        btnOpen = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceOpen);
        btnClose = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceClose);
        btnGetByPassEntires = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceBypassEntries);

        btnConnect = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceConnect);
        btnDisconnect = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceDisconnect);
        buttonRead = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceRead);
        buttonSendHeartBeat = this.rootView.findViewById(R.id.buttonFragmentInterlockDeviceHeartBeat);

        swState = this.rootView.findViewById(R.id.switchFragmentInterlockDeviceState);

        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnGetByPassEntires.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        buttonRead.setOnClickListener(this);
        buttonSendHeartBeat.setOnClickListener(this);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView tvClientNode = this.rootView.findViewById(R.id.editTextFragmentInterlockDeviceFINSClientNode);
        TextView tvServerNode = this.rootView.findViewById(R.id.editTextFragmentInterlockDeviceFINSServerNode);


        tvClientNode.setText(Integer.toString(Machine.getInstance().getCurrentFINNodeNumber()));
        tvServerNode.setText(Integer.toString(FINSSession.serverNodeAddress));
        swState.setEnabled(false);


        if (Machine.getInstance().hasComms())
            Machine.getInstance().isClosed(this);

        boolean connActive = Machine.getInstance().hasComms();

        btnDisconnect.setEnabled(connActive);
        btnConnect.setEnabled(!connActive);
        btnGetByPassEntires.setEnabled((connActive));
        btnOpen.setEnabled(connActive);
        btnClose.setEnabled(connActive);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMachineInterlockDeviceStateRead(boolean isClosed) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                swState.setChecked(isClosed);

                boolean connActive = Machine.getInstance().hasComms();

                btnOpen.setEnabled(connActive && isClosed);
                btnClose.setEnabled(connActive && !isClosed);

            }
        });

    }

    @Override
    public void onMachineInitComms(Boolean connected) {

        if (connected)
            Machine.getInstance().isClosed(this);
        else
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnOpen.setEnabled(false);
                    btnClose.setEnabled(false);

                }
            });
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDisconnect.setEnabled(connected);
                btnConnect.setEnabled(!connected);
                btnGetByPassEntires.setEnabled((connected));

            }
        });
    }

    @Override
    public void onMachineBypassReadCompleted(Object[] lines) {

        try {


            FileOutputStream fileOutputStream = getActivity().openFileOutput("bypass.csv", 0);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            for (Object line : lines) {
                ReportLineByPass reportLineByPass = (ReportLineByPass)line;
                outputStreamWriter.write(ReportLineByPass.dateFormatBCD.format(reportLineByPass.getDateTime()));
                outputStreamWriter.write(",");
                outputStreamWriter.write(Integer.toString(reportLineByPass.getEventType()));
                outputStreamWriter.write('\n');

            }

            outputStreamWriter.close();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnGetByPassEntires.setEnabled(true);

                }
            });

        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }


    }

    @Override
    public void onMachineInterlockDeviceClosed() {

        try {
            //Give time for the timers inside the PLC to execute before checking the state of the
            //interlock device.
            Thread.sleep(1000);
            Machine.getInstance().isClosed(this);
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    @Override
    public void onMachineInterlockDeviceOpened() {
        try {
            Thread.sleep(500);
            Machine.getInstance().isClosed(this);
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonFragmentInterlockDeviceConnect)
        {

            //Add a log entry
            AppLog.getInstance().eventInterlockDeviceAction("Connecting to interlock device.");
            Machine.getInstance().initComms(this,
                                            App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS),
                                            App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS));


        }
        else if (v.getId() == R.id.buttonFragmentInterlockDeviceDisconnect)
        {
            //Add log entry
            AppLog.getInstance().eventInterlockDeviceAction("Disconecting from the interlock device.");
            Machine.getInstance().endComms();
            onMachineInitComms(false);

        }
        else if (v.getId() == R.id.buttonFragmentInterlockDeviceClose)
        {
            if (Machine.getInstance().hasComms()) {
                //Add log entry.
                AppLog.getInstance().eventInterlockDeviceAction("Closed the interlock device.");
                Machine.getInstance().closeInterlockDevice(this);
            }
        }
        else if (v.getId() == R.id.buttonFragmentInterlockDeviceOpen)
        {
            if (Machine.getInstance().hasComms()) {
                //Add log entry.
                AppLog.getInstance().eventInterlockDeviceAction("Opened the interlock device.");
                Machine.getInstance().openInterlockDevice(this);
            }
        }

        else if (v.getId() == R.id.buttonFragmentInterlockDeviceRead)
        {
            if (Machine.getInstance().hasComms()) {
                //Add log entry.
                AppLog.getInstance().eventInterlockDeviceAction("Checking if the interlock device is closed or open.");
                Machine.getInstance().isClosed(this);
            }
        }
        else if (v.getId() == R.id.buttonFragmentInterlockDeviceBypassEntries)
        {
            //btnGetByPassEntires.setEnabled(false);
            //Machine.getInstance().getBypassEntries(this);
            //Before generating the  bypass report, add a log entry  to application log for the action.
            AppLog.getInstance().eventInterlockDeviceAction("Read bypass entries from the plc. ");
            WorkRequest workRequest =    OneTimeWorkRequest.from(WorkerEmailBypass.class);
            WorkManager workManager =  WorkManager.getInstance(getActivity().getApplicationContext());
            workManager.enqueue(workRequest);
        }
        else if (v.getId() == R.id.buttonFragmentInterlockDeviceHeartBeat)
        {
            //TODO: Check if the device heart beat button is usable.
            WorkRequest workRequest =    OneTimeWorkRequest.from(WorkerHeartbeat.class);
            WorkManager workManager =  WorkManager.getInstance(getActivity().getApplicationContext());
            workManager.enqueue(workRequest);

        }

    }



}