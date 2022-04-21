package com.mb.prestartcheck.console;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Display a dialog that asks for a supervisor's PIN entry.
 * The supervisor can choose his user name from the displayed
 * switch control.
 */
public class DialogSupervisorLogin extends DialogFragment {

    //Store references to child views for event handling.
    private View rootView;
    private Spinner spinnerSupervisors;
    private ViewModelFragmentLoginSupervisor viewModel;
    private TextView textViewMachineName;
    private TextView tvLoginDate;
    int countRetries = 0;
    private DialogFragmentListener listener;
    private final Fragment owner;

    public DialogSupervisorLogin(Fragment owner) {
        this.owner = owner;
    }


    /**
     * Display the "fragment_login_supervisor" layout. Then, return
     * the inflated view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Root view from the "fragment_login_supervisor" layout.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Use the ViewModelFragmentLoginSupervisor class to get a list of Supervisors
        //OnStart populates the supervisor switch.
        viewModel = new ViewModelFragmentLoginSupervisor(getActivity().getApplication());
        setCancelable(false);
        return inflater.inflate(R.layout.dialogue_fregment_login_supervisor, container);
    }

    /**
     * View created, store child view references to be used later.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        spinnerSupervisors = rootView.findViewById(R.id.spinnerFragmentLoginSupervisorName);
        textViewMachineName = rootView.findViewById(R.id.textViewFragmentLoginSupervisorMachine);
        tvLoginDate = rootView.findViewById(R.id.textViewFragmentLoginSupervisorLastLoginDate);
        Button bLogin = rootView.findViewById(R.id.buttonFragmentLoginSupervisorOK);

        EditText editTextPin = rootView.findViewById(R.id.editTextFragmentLoginSupervisorPin);

        ArrayAdapter<Supervisor> adapter = new ArrayAdapter<Supervisor>(getContext(),
                R.layout.content_generic_spinner_item,
                R.id.textViewcontentGenericSpinnerItem,
                viewModel.getSupervisors());
        spinnerSupervisors.setAdapter(adapter);

        textViewMachineName.setText(Machine.getInstance().getName());

        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss d MMM yyyy");
        tvLoginDate.setText(String.format("%s", df.format(today)));

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextPin.getText().toString().trim().isEmpty()) {
                    VerifyPin(editTextPin.getText().toString().trim());
                } else {
                    Toast.makeText(getActivity(), R.string.fragment_login_caption,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.60), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void VerifyPin(String pin) {
        Spinner spinner = rootView.findViewById(R.id.spinnerFragmentLoginSupervisorName);
        Supervisor supervisor = (Supervisor) spinner.getSelectedItem();

        if (supervisor.getPin().compareTo(pin) == 0) {
            Log.i("countRetries", "correct case");
            Toast.makeText(getActivity(), R.string.fragment_logout_login_date_time_label,
                    Toast.LENGTH_LONG).show();

            if (listener != null) listener.onPositive(DialogSupervisorLogin.this, DialogSupervisorLogin.this.owner);
            AppLog.getInstance().reportEvent(supervisor.getFullName() ,(new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())), ActionsEnum.EVENT_SUPERVISOR_ENTER_PIN_ACTION.name(), ResultEnum.RESULT_SUCCESS.name());
            dismiss();
            //Correct PIN entry should start/enable the machine

        } else {
            //Incorrect PIN entry should start/keep showing pop up
            countRetries++;
            Log.i("countRetries", "else case");
            Toast.makeText(getActivity(), R.string.error_message_wrong_pin,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            listener = (DialogFragmentListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
