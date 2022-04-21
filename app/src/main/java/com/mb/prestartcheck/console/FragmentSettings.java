package com.mb.prestartcheck.console;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Settings;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentSettings extends Fragment implements View.OnClickListener {

    private View rootView;
    private ViewModelFragmentSettings viewModel;
    private EditText editMachineName;
    private EditText editMachineOperatingHours;
    private Switch switchEmailReponses;
    private Switch switchEmailBypasses;
    private Button btnClearLogo;
    private Button btnOpHourRead;
    private Button btnOpHourWrite;
    private ImageView imageCompanyLogo;
    private TextView textLabelOpHours;

    private String uriCompanyLogo = "";
    private Bitmap bmCompanyLogo;
    private boolean clearCompanyLogo = false;

    public static final Uri locationForPhotos = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

    String machineNameBeforeChange = "";
    String machineOperatingHoursBeforeChange = "";
    boolean emailResponseBeforeChange = true;
    boolean emailBypassBeforeChange = true;
    Uri uriCompanyLogoBeforeChange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        editMachineName = this.rootView.findViewById(R.id.editTextSettingsMachineName);
        switchEmailReponses = this.rootView.findViewById(R.id.switchSettingsEmailResponses);
        switchEmailBypasses = this.rootView.findViewById(R.id.switchSettingsEmailByPasses);
        imageCompanyLogo = this.rootView.findViewById(R.id.imageViewSettingsCompanyLogo);
        btnClearLogo = this.rootView.findViewById(R.id.buttonSettingsClearCompanyLogo);
        btnOpHourRead = this.rootView.findViewById(R.id.buttonSettingsOpHoursRead);
        btnOpHourWrite = this.rootView.findViewById(R.id.buttonSettingsOpHoursWrite);
        editMachineOperatingHours = this.rootView.findViewById(R.id.editTextSettingsMachineOperatingHours);
        textLabelOpHours = this.rootView.findViewById(R.id.textViewSettingsPLCOpHours);
        this.viewModel = new ViewModelFragmentSettings(getActivity().getApplication());

        this.imageCompanyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCompanyLogo();
            }
        });

        btnClearLogo.setOnClickListener(this);

        //Assign click listerners to the "operating hours" buttons.
        btnOpHourRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disable the button first.
                btnOpHourRead.setEnabled(false);

                viewModel.readOpHours(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Set the text propertu of the "textViewSettingsPLCOpHours" TextView.
                                String label =  Integer.toString(ProxyInterlockDevice.getLastReadingOpHours())+ " minutes";
                                textLabelOpHours.setText(label);
                                //Reenable the button.
                                btnOpHourRead.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
        //Write to operating hours click listener

        btnOpHourWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate the entry for "operating hours".
                if (!validateInput()) return;

                int iOpHours = Integer.parseInt(editMachineOperatingHours.getText().toString().trim());
                //Convert the value of iOpHours to minutes.
                iOpHours*=60;
                //Proceed when there is iOpHours is not empty.
                if (iOpHours > 0) {
                    btnOpHourWrite.setEnabled(false);

                    viewModel.setOpHours(iOpHours, new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(FragmentSettings.this.getView(), R.string.settings_plc_op_hours_set, Snackbar.LENGTH_LONG );

                            //Allowed on the UI thread only.
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnOpHourWrite.setEnabled(true);
                                }
                            });

                        }
                    });
                }
            }
        });


        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(App.TAG, "FragmentSettings started,");

        //The user could be answering questions before navigating here, so pause
        //the questioning.
        Questioner.getInstance().pause();

        editMachineName.setText(App.getInstance().getSettings().get(Settings.MACHINE_NAME));
        switchEmailReponses.setChecked(Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_RESPONSES)));
        switchEmailBypasses.setChecked(Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_BYPASSES)));

        editMachineOperatingHours.setText(App.getInstance().getSettings().get(Settings.TIMEOUT_MACHINE_OPERATING_HOURS));

        if (Session.getInstance().hasPendingImageSelection()) {
            uriCompanyLogo = Session.getInstance().getLastImageSelectionParameters().getUri();
            bmCompanyLogo = Session.getInstance().getLastImageSelectionParameters().getThumbnail();
            clearCompanyLogo = false;
            Session.getInstance().clearLastParameterImageSelection();
        }

        if (uriCompanyLogo.isEmpty() && !App.getInstance().getSettings().get(Settings.COMPANY_LOGO).isEmpty()) {
            imageCompanyLogo.setImageURI(Uri.parse(App.getInstance().getSettings().get(Settings.COMPANY_LOGO)));
        } else if (!uriCompanyLogo.isEmpty()) {
            imageCompanyLogo.setImageBitmap(bmCompanyLogo);
        } else {
            imageCompanyLogo.setImageResource(R.drawable.outline_image_24);
        }
        switchEmailBypasses.setChecked(Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_BYPASSES)));

        editMachineOperatingHours.setText(App.getInstance().getSettings().get(Settings.TIMEOUT_MACHINE_OPERATING_HOURS));

        machineNameBeforeChange = App.getInstance().getSettings().get(Settings.MACHINE_NAME);
        machineOperatingHoursBeforeChange = App.getInstance().getSettings().get(Settings.TIMEOUT_MACHINE_OPERATING_HOURS);
        emailResponseBeforeChange = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_RESPONSES));
        emailBypassBeforeChange = Boolean.parseBoolean(App.getInstance().getSettings().get(Settings.EMAIL_BYPASSES));
        uriCompanyLogoBeforeChange = Uri.parse(App.getInstance().getSettings().get(Settings.COMPANY_LOGO));
    }

    private void reportUserChanges(String machineName,
                                   String machineOperatingHours,
                                   boolean emailBypassSwitch,
                                   boolean emailResponseSwitch,
                                   Uri uriCompanyLogo) {

        // check to identify which fields were changed on this
        if (!machineNameBeforeChange.equals(machineName)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Enter the name of the machine",
                    machineNameBeforeChange,
                    machineName);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!machineOperatingHoursBeforeChange.equals(machineOperatingHours)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Enter the maximum amount of time between shifts that a user can be logged in for (hours)",
                    machineOperatingHoursBeforeChange,
                    machineOperatingHours);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!uriCompanyLogoBeforeChange.equals(uriCompanyLogo)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Company logo:",
                    String.valueOf(uriCompanyLogoBeforeChange),
                    String.valueOf(uriCompanyLogo));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (emailResponseBeforeChange != emailResponseSwitch) {
            AppLog.getInstance().updateValuesChangeString(
                    "Email responses.",
                    String.valueOf(emailResponseBeforeChange),
                    String.valueOf(emailResponseSwitch));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (emailBypassBeforeChange != emailBypassSwitch) {
            AppLog.getInstance().updateValuesChangeString(
                    "Email bypasses.",
                    String.valueOf(emailBypassSwitch),
                    String.valueOf(emailResponseSwitch));
            AppLog.getInstance().isValueUpdated = true;
        }

        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_MACHINE_SETTINGS_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
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
                if (!validateInput()) return true;

                String companyLogo = "";
                if (clearCompanyLogo) companyLogo = "";
                else if (!uriCompanyLogo.isEmpty()) companyLogo = uriCompanyLogo;
                else companyLogo = App.getInstance().getSettings().get(Settings.COMPANY_LOGO);


                this.viewModel.updateSettings(editMachineName.getText().toString().trim(),
                        switchEmailReponses.isChecked(),
                        switchEmailBypasses.isChecked(),
                        companyLogo,
                        editMachineOperatingHours.getText().toString().trim()
                        , new Settings.OnSavedHandler() {
                            @Override
                            public void saved() {

                                FragmentSettings.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogMessage dialogMessage = new DialogMessage("Settings saved.", getResources().getString(R.string.ok));
                                        dialogMessage.show(getParentFragmentManager(), "settings_saved");

                                    }
                                });
                            }
                        });

                reportUserChanges( editMachineName.getText().toString().trim(),
                        editMachineOperatingHours.getText().toString().trim(),
                        switchEmailBypasses.isChecked(),
                        switchEmailReponses.isChecked(),
                        Uri.parse(companyLogo)  );

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean validateInput() {
        try {
            UIValidator.checkTextEntry(editMachineName, getView(), R.string.settings_error_machine_name);
            UIValidator.checkNumberRange(editMachineOperatingHours, getView(), R.string.settings_error_machine_operating_hours, 24, 1);

            return true;
        } catch (UIFormatException ex) {
            ex.getView().requestFocus();
        }

        return false;
    }

    private void setCompanyLogo() {
        try {


            Bundle bundle = new Bundle();
            bundle.putString("section_title", "Select company logo");
            bundle.putLong("question_id", -1);
            bundle.putInt("image_index", 1);

            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_image_select, bundle);
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    public void setCompanyLogo(final Bitmap bitmap) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageCompanyLogo.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSettingsClearCompanyLogo) {
            clearCompanyLogo = true;
            imageCompanyLogo.setImageResource(R.drawable.outline_image_24);
        }
    }
}
