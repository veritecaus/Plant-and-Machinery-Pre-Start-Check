package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Emailer;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentSettingsEmailServer extends Fragment {
    private View rootView;
    private ViewModelFragmentSettingsEmailServer viewModel;
    private EditText eUserName;
    private EditText ePassword;
    private EditText ePort;
    private Button buttonTestEmail;
    private String toEmailAddress;
    private Boolean testEmailFlow = false;
    private Switch switchRequiresSignIn;
    boolean isNew = false;
    boolean requiredSignIn = false;
    String emailAddress = "";
    String eFromAddress = "";
    int port;
    String selectedItem = "";
    String userName = "";
    String password = "";
    String serverSMTP = "";
    String result = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_settings_email_server, container, false);
        this.viewModel = new ViewModelFragmentSettingsEmailServer(this.getActivity().getApplication());
        switchRequiresSignIn = this.rootView.findViewById(R.id.switchFragmentSettingsEmailServerSignIn);
        eUserName = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerUserName);
        ePassword = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerPassword);
        ePort = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerPort);
        buttonTestEmail = this.rootView.findViewById(R.id.buttonTestEmail);

        switchRequiresSignIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                eUserName.setEnabled(isChecked);
                ePassword.setEnabled(isChecked);
            }
        });
        buttonTestEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnterEmailDialogue();
            }
        });

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        toEmailAddress = App.getInstance().getSettings().get(Settings.EMAIl_TO);
        EditText eFromAddr = this.rootView.findViewById(R.id.editTextFragmentEmailFromAddr);
        EditText eSMTP = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerSMTP);
        EditText ePort = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerPort);
        Spinner spinner = this.rootView.findViewById(R.id.spinnerFragmentSettingsEmailServerSecurityType);

        String[] items = new String[]{"TLS", "SSL", "None"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        eFromAddr.setText(App.getInstance().getSettings().get(Settings.EMAIl_FROM));
        eSMTP.setText(App.getInstance().getSettings().get(Settings.EMAIl_SMTP));
        int smtpPort = Integer.parseInt(App.getInstance().getSettings().get(Settings.EMAIl_SMTP_PORT));

        ePort.setText(Integer.toString(smtpPort));

        Log.i(App.TAG, App.getInstance().getSettings().get(Settings.EMAIL_SECURITY_TYPE));

        if (App.getInstance().getSettings().get(Settings.EMAIL_SECURITY_TYPE).compareTo("tls") == 0)
            spinner.setSelection(0);
        else if (App.getInstance().getSettings().get(Settings.EMAIL_SECURITY_TYPE).compareTo("ssl") == 0)
            spinner.setSelection(1);
        else
            spinner.setSelection(2);

        boolean requiresSignIn = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIl_REQUIRES_SIGNIN));
        switchRequiresSignIn.setChecked(requiresSignIn);

        eUserName.setText(App.getInstance().getSettings().get(Settings.EMAIL_USER_NAME));
        ePassword.setText(App.getInstance().getSettings().get(Settings.EMAIL_USER_PASSWORD));
        eUserName.setEnabled(requiresSignIn);
        ePassword.setEnabled(requiresSignIn);

        if (null == eFromAddr.getText().toString().trim()) {
            isNew = true;
        } else {
            saveOldStrings(eFromAddr, eSMTP, spinner, smtpPort, requiresSignIn);
        }
    }

    private void saveOldStrings(EditText eFromAddr, EditText eSMTP, Spinner spinner, int smtpPort, boolean requiresSignIn) {
        requiredSignIn = requiresSignIn;
        emailAddress = eUserName.getText().toString();
        eFromAddress = eFromAddr.getText().toString();
        port = smtpPort;
        selectedItem = spinner.getSelectedItem().toString();
        userName = eUserName.getText().toString();
        password = ePassword.getText().toString();
        serverSMTP = eSMTP.getText().toString();

    }

    private void reportUserChanges(String toEmailAddress, String fromAddress, String smtpServer, int smtpPort,
                                   String securityType, boolean requiresSignIn,
                                   String username, String pass) {

        // check to identify which fields were changed on this
        if (!emailAddress.equals(toEmailAddress)) {
            AppLog.getInstance().updateValuesChangeString(
                    "To address",
                    emailAddress,
                    toEmailAddress);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!eFromAddress.equals(fromAddress)) {
            AppLog.getInstance().updateValuesChangeString(
                    "From address",
                    eFromAddress,
                    fromAddress);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!userName.equals(username)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Username",
                    userName,
                    username);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!password.equals(pass)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Password",
                    password,
                    pass);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!serverSMTP.equals(smtpServer)) {
            AppLog.getInstance().updateValuesChangeString(
                    "   SMTP server",
                    serverSMTP,
                    smtpServer);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!selectedItem.equals(securityType)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Security Type",
                    selectedItem,
                    securityType);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!String.valueOf(port).equals(smtpPort)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Port",
                    String.valueOf(port),
                    String.valueOf(smtpPort)
            );
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!requiredSignIn == requiresSignIn) {
            AppLog.getInstance().updateValuesChangeString(
                    "Requires sign-in",
                    String.valueOf(requiredSignIn),
                    String.valueOf(requiresSignIn));
            AppLog.getInstance().isValueUpdated = true;
        }

        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_EMAIL_SERVER_SETTINGS_ACTION.name(),
                    result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_common_actions, menu);
        menu.findItem(R.id.action_menu_item_common_delete).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_item_common_done: {
                if (!validateInputs()) return true;
                done();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void done() {
        EditText eFromAddr = this.rootView.findViewById(R.id.editTextFragmentEmailFromAddr);
        EditText eSMTP = this.rootView.findViewById(R.id.editTextFragmentSettingsEmailServerSMTP);
        Spinner spinner = this.rootView.findViewById(R.id.spinnerFragmentSettingsEmailServerSecurityType);

        this.viewModel.update(toEmailAddress, eFromAddr.getText().toString(), eSMTP.getText().toString(),
                Integer.parseInt(ePort.getText().toString()),
                spinner.getSelectedItem().toString().toLowerCase(),
                switchRequiresSignIn.isChecked(),
                eUserName.getText().toString(),
                ePassword.getText().toString(),
                new Settings.OnSavedHandler() {
                    @Override
                    public void saved() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (testEmailFlow) {
                                    boolean sent = Emailer.sendWithAttachment(null, "Test", new String[]{toEmailAddress});
                                    if (sent) {
                                        AppLog.getInstance().print("WorkerEmailResponse sent email.");
                                        Toast toast = Toast.makeText(getActivity(), "Test email sent successfully", Toast.LENGTH_SHORT);
                                        toast.show();
                                        result = ResultEnum.RESULT_SUCCESS.name();
                                    } else {
                                        AppLog.getInstance().print("WorkerEmailResponse failed to send email");
                                        Toast toast = Toast.makeText(getActivity(), "Failed to send test email", Toast.LENGTH_SHORT);
                                        toast.show();
                                        result = ResultEnum.RESULT_FAIL.name();
                                    }
                                } else {
                                    final NavController navController = NavHostFragment.findNavController(FragmentSettingsEmailServer.this);
                                    navController.popBackStack();
                                }
                            }
                        });
                    }
                }
        );

        if (!isNew) {
            reportUserChanges(toEmailAddress, eFromAddr.getText().toString(), eSMTP.getText().toString(),
                    Integer.parseInt(ePort.getText().toString()),
                    spinner.getSelectedItem().toString().toLowerCase(),
                    switchRequiresSignIn.isChecked(),
                    eUserName.getText().toString(),
                    ePassword.getText().toString());
        } else {
            AppLog.getInstance().reportEvent(
                    Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_EMAIL_SERVER_SETTINGS_ACTION.name(),
                    result);
        }
    }

    private boolean validateInputs() {
        try {
            UIValidator.checkNumberEntry(ePort, this.getView(), R.string.caption_settings_email_server_smtp_invalid_port);
            return true;

        } catch (UIFormatException fex) {
            //TODO : give focus to control
            fex.getView().requestFocus();
        }
        return false;
    }

    public void showEnterEmailDialogue() {
        AlertDialog.Builder enterEmailPopUp = new AlertDialog.Builder(getActivity());

        enterEmailPopUp.setTitle(getString(R.string.caption_settings_to_email_pop_up_title));
        enterEmailPopUp.setMessage(getString(R.string.caption_settings_to_email_pop_up_message));

        final EditText input = new EditText(getActivity());
        input.setText(toEmailAddress);
        enterEmailPopUp.setView(input);

        enterEmailPopUp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().isEmpty()) {
                    testEmailFlow = true;

                    toEmailAddress = input.getText().toString();
                    done();
                }
            }
        });

        enterEmailPopUp.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        enterEmailPopUp.show();
    }
}
