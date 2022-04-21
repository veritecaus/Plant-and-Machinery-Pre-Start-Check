package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.DateFormatScreen;
import com.mb.prestartcheck.Operator;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Role;
import com.mb.prestartcheck.Roles;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.User;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentUserAdd extends Fragment {
    ViewModelUserAdd viewModel;
    View rootView;

    String firstNameBeforeChange = "";
    String lastNameBeforeChange = "";
    String pinBeforeChange = "";
    String roleBeforeChange = "";
    boolean isEnabledBeforeChange;
    boolean isNew = false;

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextPin;
    Switch switchEnabled;
    Spinner spinner;
    TextView tvLastLogin;
    TextView tvCreatedDateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user_add, container, false);

        long userId = getArguments().getLong("user_id", -1);
        viewModel = new ViewModelUserAdd(getActivity().getApplication(), userId);
        if (String.valueOf(userId).equals("-1")) {
            isNew = true;
        }
        getRoles();

        return this.rootView;
    }

    private void initFields() {

        switchEnabled = this.rootView.findViewById(R.id.switchOperatorAddEnabled);
        editTextFirstName = this.rootView.findViewById(R.id.editTextOperatorAddFirstName);
        editTextLastName = this.rootView.findViewById(R.id.editTextOperatorAddLastName);
        editTextPin = this.rootView.findViewById(R.id.editTextOperatorAddPin);

        spinner = this.rootView.findViewById(R.id.spinnerOperatorAddRole);
        tvLastLogin = this.rootView.findViewById(R.id.textViewCaptionOperatorAddLastLogin);
        tvCreatedDateTime = this.rootView.findViewById(R.id.textViewCaptionOperatorAddCreatedDateTime);

    }

    @Override
    public void onStart() {
        super.onStart();

        User user = this.viewModel.getUser();
        initFields();

        firstNameBeforeChange = (user == null ? "" : user.getFirstName());
        lastNameBeforeChange = (user == null ? "" : user.getLastName());
        pinBeforeChange = (user == null ? "" : user.getPin());
        isEnabledBeforeChange = (user == null ? true : user.getEnabled());

        switchEnabled.setChecked((user == null ? true : user.getEnabled()));
        editTextFirstName.setText(user == null ? "" : user.getFirstName());
        editTextLastName.setText(user == null ? "" : user.getLastName());
        editTextPin.setText(user == null ? "" : user.getPin());

        boolean isOp = user != null && Operator.class.isAssignableFrom(user.getClass());
        boolean isSuper = user != null && Supervisor.class.isAssignableFrom(user.getClass());

        if (isOp) {
            spinner.setSelection(0);
            roleBeforeChange = "Operator";
        } else if (isSuper) {
            spinner.setSelection(1);
            roleBeforeChange = "Supervisor";
        }
        DateFormatScreen dateFormatScreen = new DateFormatScreen();
        String lastlog = String.format("Last logon %s", user == null ? "" : dateFormatScreen.format(user.getLastLogin()));
        tvLastLogin.setText(lastlog);

        String createdon = String.format("Create on %s", user == null ? "" : dateFormatScreen.format(user.getCreatedDateTime()));
        tvCreatedDateTime.setText(createdon);

    }

    private void getRoles() {
        Spinner spinner = this.rootView.findViewById(R.id.spinnerOperatorAddRole);

        ArrayAdapter<Role> spinnerArrayAdapter = new ArrayAdapter<Role>
                (this.getActivity(), android.R.layout.simple_spinner_item, Roles.getInstance().getList());

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private boolean validateForm() {
        if (editTextFirstName.getText().length() == 0) {
            Snackbar.make(this.rootView, R.string.validation_message_operator_add_missing_first_name, Snackbar.LENGTH_LONG).show();

            editTextFirstName.selectAll();
            editTextFirstName.requestFocus();
            return false;
        } else if (editTextLastName.getText().length() == 0) {
            Snackbar.make(this.rootView, R.string.validation_message_operator_add_missing_last_name, Snackbar.LENGTH_LONG).show();

            editTextLastName.selectAll();
            editTextLastName.requestFocus();

            return false;
        } else if (editTextPin.getText().length() < 4) {
            Snackbar.make(this.rootView, R.string.validation_message_pin_number, Snackbar.LENGTH_LONG).show();

            editTextPin.selectAll();
            editTextPin.requestFocus();
            return false;
        }
        return true;
    }

    private void saveToDatabase() {
        try {
            if (validateForm()) {
                Runnable oncomplete = new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NavController navController = NavHostFragment.findNavController(FragmentUserAdd.this);
                                navController.popBackStack();
                            }
                        });
                    }
                };

                this.viewModel.saveUser(editTextFirstName.getText().toString().trim(),
                        editTextLastName.getText().toString().trim(),
                        editTextPin.getText().toString().trim(),
                        switchEnabled.isChecked(),
                        (Role) spinner.getSelectedItem(),
                        oncomplete);

                if (!isNew) {
                    reportUserChanges(editTextFirstName,
                            editTextLastName,
                            editTextPin,
                            switchEnabled.isChecked(),
                            ((Role) spinner.getSelectedItem()).getLabel());
                } else {
                    AppLog.getInstance().reportEvent(
                            Session.getInstance().getUser().getFullName(),
                            (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_USER_INFORMATION_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());
                }
            }
        } catch (Exception ex) {
            Log.e("prestartcheck", ex.getMessage());
        }

    }

    private void reportUserChanges(EditText editTextFirstName,
                                   EditText editTextLastName,
                                   EditText pin,
                                   boolean enabled,
                                   String role) {

        // check to identify which fields were changed on this
        if (!firstNameBeforeChange.equals(editTextFirstName.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "First Name",
                    firstNameBeforeChange,
                    editTextFirstName.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!lastNameBeforeChange.equals(editTextLastName.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Last Name",
                    lastNameBeforeChange,
                    editTextLastName.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!pinBeforeChange.equals(pin.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Pin",
                    pinBeforeChange,
                    pin.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!roleBeforeChange.equals(role)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Role",
                    roleBeforeChange,
                    role);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!isEnabledBeforeChange == enabled) {
            AppLog.getInstance().updateValuesChangeString(
                    "Enable:",
                    String.valueOf(isEnabledBeforeChange),
                    String.valueOf(enabled));
            AppLog.getInstance().isValueUpdated = true;
        }

        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_USER_INFORMATION_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu_item_common_done: {
                saveToDatabase();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {


        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_common_actions, menu);

        menu.findItem(R.id.action_menu_item_common_delete).setVisible(false);

    }
}
