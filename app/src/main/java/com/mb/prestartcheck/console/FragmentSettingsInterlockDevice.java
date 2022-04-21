package com.mb.prestartcheck.console;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.ReportLineByPass;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.fins.FINSFrameWriteWR;
import com.mb.prestartcheck.fins.FINSSession;
import com.mb.prestartcheck.fins.FINSTCPHeaderCommand;

import org.jetbrains.annotations.NotNull;


public class FragmentSettingsInterlockDevice extends Fragment implements  Settings.OnSavedHandler, View.OnClickListener {
    private  View rootView;
    private ViewModelFragmentSettingsInterlockDevice viewModel;
    private EditText editAltFINSNode;
    private EditText editFINSNode;
    private EditText editInterlockAddr;
    private Button buttonTest;
    private TextView textResults;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView =inflater.inflate(R.layout.fragment_settings_interlock_device, container, false);
        this.viewModel = new ViewModelFragmentSettingsInterlockDevice(this.getActivity().getApplication());

        //When reading bypasses from the interlock device, connect to the interlock device and assign
        //a different node number than the control connnection.
        editAltFINSNode = this.rootView.findViewById(R.id.editTextFragmentSettingsInterlockDeviceAltFINSNode);

        //IP address of the interlock device.
        editInterlockAddr  = this.rootView.findViewById(R.id.editTextFragmentSettingsInterlockDeviceAddr);

        //The  client node number assigned to the controlling connection.
        editFINSNode = this.rootView.findViewById(R.id.editTextFragmentSettingsInterlockDeviceFINSNodeNo);
        buttonTest = this.rootView.findViewById(R.id.buttonFragmentSettingsInterlockDeviceTest);
        textResults = this.rootView.findViewById(R.id.textViewFragmentSettingsInterlockDeviceTestResult);
        buttonTest.setOnClickListener(this);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Make sure we restart the questioner.
        Questioner.getInstance().restart(true);

        editInterlockAddr.setText(App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS));
        editFINSNode.setText(App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS));
        editAltFINSNode.setText(App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_ALT_NODE_ADDRESS));

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void saved() {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                textResults.setText("");
                textResults.setTextColor(Color.BLACK);

            }
        });

        DialogMessage dialogMessage = new DialogMessage("Interlock device settings saved.", "OK");
        dialogMessage.show(getParentFragmentManager(),"saved_message");


    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_interlock_device, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_menu_item_interlock_device_done)
        {
            if (!validateInput()) return true;

            this.viewModel.update(this.editInterlockAddr.getText().toString().trim(),
                                this.editFINSNode.getText().toString().trim(),
                                this.editAltFINSNode.getText().toString().trim(),
                    this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentSettingsInterlockDeviceTest)
        {
            if (!validateInput()) return;

            textResults.setText("Communicating with device. Please wait....");
            textResults.setTextColor(Color.BLACK);

            this.viewModel.testSettings(editInterlockAddr.getText().toString().trim(),
                    Integer.parseInt(editFINSNode.getText().toString().trim()),
                    new ProxyInterlockDevice.ProxyInterlockDeviceListener() {

                        @Override
                        public void proxyInterlockDeviceListeneronCommsInit(boolean isConnected) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textResults.setText(isConnected ? R.string.fragment_settings_interlock_device_test_result_sucess :
                                            R.string.fragment_settings_interlock_device_test_result_failed);

                                    textResults.setTextColor(isConnected ? Color.GREEN : Color.RED);

                                }
                            });

                        }

                    });
        }
    }

    private boolean validateInput()
    {
        try {
            UIValidator.checkIpEntry(editInterlockAddr, getView(), R.string.validation_invalid_tcp_address);
            UIValidator.checkNumberEntry(editFINSNode, getView(), R.string.validation_invalid_number_entry);
            UIValidator.checkNumberEntry(editAltFINSNode, getView(), R.string.validation_invalid_number_entry);
            UIValidator.checkDuplicateEntries(editFINSNode, editAltFINSNode, getView(), R.string.validation_duplicate_fins_nodes);

            //Check if the FINS node number is too high
            UIValidator.checkNumberRange(editFINSNode,  getView(), R.string.validation_fins_node_entry_out_range, 250, 0);
            UIValidator.checkNumberRange(editAltFINSNode, getView(), R.string.validation_fins_node_entry_out_range, 250, 0);

            return true;
        }
        catch(UIFormatException ex)
        {
            ex.getView().requestFocus();
        }

        return false;

    }



}
