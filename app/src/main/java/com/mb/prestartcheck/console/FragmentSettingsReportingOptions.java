package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class FragmentSettingsReportingOptions extends Fragment implements Settings.OnSavedHandler {

    private View rootView;
    private ViewModelFragmentSettingsReportingOptions viewModel;
    String recipientOne = "";
    String recipientTwo = "";
    String recipientThree = "";
    String recipientFour = "";
    boolean isNew = false;

    public FragmentSettingsReportingOptions() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_settings_reporting_options, container, false);
        viewModel = new ViewModelFragmentSettingsReportingOptions(getActivity().getApplication());

        Spinner spinnerFrequency = this.rootView.findViewById(R.id.spinnerFragmentSettingsReportingOptionsFreq);
        String[] items = new String[]{"daily", "weekly", "fortnightly", "monthly"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(arrayAdapter);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EditText eRep1 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep1);
        EditText eRep2 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep2);
        EditText eRep3 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep3);
        EditText eRep4 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep4);
        Spinner spinnerFrquency = this.rootView.findViewById(R.id.spinnerFragmentSettingsReportingOptionsFreq);

        eRep1.setText(App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_ONE));
        eRep2.setText(App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_TWO));
        eRep3.setText(App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_THREE));
        eRep4.setText(App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FOUR));

        String frequency = App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FREQUENCY);

        if (frequency.compareTo("daily") == 0) spinnerFrquency.setSelection(0);
        else if (frequency.compareTo("weekly") == 0) spinnerFrquency.setSelection(1);
        else if (frequency.compareTo("fortnightly") == 0) spinnerFrquency.setSelection(2);
        else if (frequency.compareTo("monthly") == 0) spinnerFrquency.setSelection(3);

        if (
                (App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_ONE).equals("report_recipient_one") || App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_ONE).equals("")) &&
                        (App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_TWO).equals("report_recipient_two") || App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_TWO).equals("")) &&
                        (App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_THREE).equals("report_recipient_three") || App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_THREE).equals("")) &&
                        (App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FOUR).equals("report_recipient_four") || App.getInstance().getSettings().get(Settings.REPORT_RECIPIENT_FOUR).equals(""))
        ) {
            isNew = true;
        } else {
            isNew = false;
        }
        recipientOne = eRep1.getText().toString().trim();
        recipientTwo = eRep2.getText().toString().trim();
        recipientThree = eRep3.getText().toString().trim();
        recipientFour = eRep4.getText().toString().trim();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void saved() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DialogMessage dialogMessage = new DialogMessage("Settings saved.", "OK");
                dialogMessage.show(getParentFragmentManager(), "dialog_message");

            }
        });
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
                EditText eRep1 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep1);
                EditText eRep2 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep2);
                EditText eRep3 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep3);
                EditText eRep4 = this.rootView.findViewById(R.id.editTextFragmentSettingsReportingOptionsRep4);

                Spinner spinnerFrquency = this.rootView.findViewById(R.id.spinnerFragmentSettingsReportingOptionsFreq);

                this.viewModel.updateSettings(eRep1.getText().toString().trim(),
                        eRep2.getText().toString().trim(),
                        eRep3.getText().toString().trim(),
                        eRep4.getText().toString().trim()
                        , spinnerFrquency.getSelectedItem().toString(),
                        this);

                if (!isNew) {
                    reportQuestionChanges(eRep1.getText().toString().trim(),
                            eRep2.getText().toString().trim(),
                            eRep3.getText().toString().trim(),
                            eRep4.getText().toString().trim());
                } else {
                    AppLog.getInstance().reportEvent(
                            Session.getInstance().getUser().getFullName(),
                            (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_EMAIL_OPTIONS_SETTINGS_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());
                }

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reportQuestionChanges(String rep1, String rep2, String rep3, String rep4) {
        // check to identify which fields were changed on this
        if (!recipientOne.equals(rep1)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Recipient 1",
                    recipientOne,
                    rep1);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!recipientTwo.equals(rep2)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Recipient 2",
                    recipientTwo,
                    rep2);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!recipientThree.equals(rep3)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Recipient 3",
                    recipientThree,
                    rep3);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!recipientFour.equals(rep4)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Recipient 4",
                    recipientFour,
                    rep4);
            AppLog.getInstance().isValueUpdated = true;
        }
        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_EMAIL_OPTIONS_SETTINGS_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
        }
    }
}