package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentLoginSupervisor extends Fragment implements View.OnClickListener {
    private  View rootView;
    private ViewModelFragmentLoginSupervisor viewModel;
    private EditText editTextPin;
    private boolean showRiskNotice;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_supervisor, container, false);
        //TODO: Store references to child views in the "onViewCreated" method override.
        showRiskNotice = getArguments() != null ? getArguments().getBoolean("show_risk_notice", true) : true;

        viewModel = new ViewModelFragmentLoginSupervisor(getActivity().getApplication());
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Spinner spinner = this.rootView.findViewById(R.id.spinnerFragmentLoginSupervisorName);
        ArrayAdapter<Supervisor> adapter = new ArrayAdapter<Supervisor>(getContext(),
                R.layout.content_generic_spinner_item,
                R.id.textViewcontentGenericSpinnerItem,
                viewModel.getSupervisors());
        spinner.setAdapter(adapter);

        TextView textViewMachineName = this.rootView.findViewById(R.id.textViewFragmentLoginSupervisorMachine);
        textViewMachineName.setText(Machine.getInstance().getName());

        TextView tvLoginDate = rootView.findViewById(R.id.textViewFragmentLoginSupervisorLastLoginDate);
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss d MMM yyyy");
        tvLoginDate.setText(String.format("%s", df.format(today)));

        Button bLogin =  rootView.findViewById(R.id.buttonFragmentLoginSupervisorOK);
        Button bCancel = rootView.findViewById(R.id.buttonFragmentLoginSupervisorCancel);

        editTextPin = rootView.findViewById(R.id.editTextFragmentLoginSupervisorPin);

        bLogin.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        bCancel.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {

        //Hide the on screen keyboard.
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPin.getWindowToken(), 0);

        if (v.getId() == R.id.buttonFragmentLoginSupervisorCancel)
        {
                //Pop the navigation stack.
                final NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
                return;
        }
        else if (v.getId() == R.id.buttonFragmentLoginSupervisorOK)
        {
            Spinner spinner = this.rootView.findViewById(R.id.spinnerFragmentLoginSupervisorName);
            Supervisor supervisor = (Supervisor)spinner.getSelectedItem();

            EditText editTextPin = rootView.findViewById(R.id.editTextFragmentLoginSupervisorPin);

            String enteredPin = editTextPin.getText().toString().trim();
            if (supervisor.getPin().compareTo(enteredPin) == 0)
            {
                //Correct PIN entry
                AppLog.getInstance().reportEvent(supervisor.getFullName() ,(new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())), ActionsEnum.EVENT_USER_LOGIN_ACTION.name(), ResultEnum.RESULT_SUCCESS.name());

                if (showRiskNotice) {
                    FragmentLoginSupervisorDirections.ActionNavLoginSupervisorToNavRiskNotice
                            action = FragmentLoginSupervisorDirections.actionNavLoginSupervisorToNavRiskNotice(supervisor.getId());
                    final NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(action);
                }
                else
                {
                    final NavDirections navDirections =  FragmentLoginSupervisorDirections.actionNavLoginSupervisorToNavLogout();
                    final NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(navDirections);

                }
            }
            else
            {
                //Incorrect PIN entry
                Snackbar.make(this.getView(), R.string.error_message_wrong_pin, Snackbar.LENGTH_LONG );
            }
        }
    }
}
