package com.mb.prestartcheck.console;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.DateFormaterEntry;
import com.mb.prestartcheck.FileName;
import com.mb.prestartcheck.ParameterReport;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.WorkerEmailResponse;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentReport extends Fragment implements View.OnClickListener {

    public static final int CREATE_FILE = 1;
    private ViewModelFragmentReport viewModel;
    private View rootView;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Button btnExport;
    private Button btnEmail;
    private Spinner spinnerSDDay;
    private Spinner spinnerSDMonth;
    private Spinner spinnerSDYear;
    private Spinner spinnerEDDay;
    private Spinner spinnerEDMonth;
    private Spinner spinnerEDYear;

    private Switch switchGetAll;
    private Switch switchStartDate;
    private Switch switchEndDate;

    private final long MILLISECONDS_PER_DAY = 86400000;

    private AdapterView.OnItemSelectedListener spinnerListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_report, container, false);

        viewModel = new ViewModelFragmentReport(getActivity().getApplication());

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        try {
                            FileInputStream fis = getActivity().openFileInput("report.csv");
                            OutputStreamWriter os = new OutputStreamWriter(getActivity().getContentResolver().openOutputStream(result));

                            while (fis.available() > 0)
                                os.write(fis.read());
                            os.close();
                            fis.close();
                        } catch (Exception ex) {
                            Log.e(App.TAG, ex.getMessage());
                        }

                        Button buttonExport = FragmentReport.this.rootView.findViewById(R.id.buttonFragmentReportsExport);
                        buttonExport.setEnabled(true);
                    }
                });

        btnExport = this.rootView.findViewById(R.id.buttonFragmentReportsExport);
        btnEmail = this.rootView.findViewById(R.id.buttonFragmentReportsEmail);
        btnExport.setOnClickListener(this);
        btnEmail.setOnClickListener(this);

        spinnerSDDay = this.rootView.findViewById(R.id.spinnerFragmentReportsSDDay);
        spinnerSDMonth = this.rootView.findViewById(R.id.spinnerFragmentReportsSDMonth);
        spinnerSDYear = this.rootView.findViewById(R.id.spinnerFragmentReportsSDYear);

        spinnerEDDay = this.rootView.findViewById(R.id.spinnerFragmentReportsEDDay);
        spinnerEDMonth = this.rootView.findViewById(R.id.spinnerFragmentReportsEDMonth);
        spinnerEDYear = this.rootView.findViewById(R.id.spinnerFragmentReportsEDYear);

        switchGetAll = this.rootView.findViewById(R.id.switchFragmentReportGetAll);
        switchStartDate = this.rootView.findViewById(R.id.switchFragmentReportsStartDate);
        switchEndDate = this.rootView.findViewById(R.id.switchFragmentReportsEndDate);


        switchGetAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean enable = !isChecked;
                spinnerSDDay.setEnabled(enable);
                spinnerSDMonth.setEnabled(enable);
                spinnerSDYear.setEnabled(enable);

                spinnerEDDay.setEnabled(enable);
                spinnerEDMonth.setEnabled(enable);
                spinnerEDYear.setEnabled(enable);

                switchStartDate.setEnabled(enable);
                switchEndDate.setEnabled(enable);


            }
        });


        Calendar cal = Calendar.getInstance();
        int noDays = cal.getMaximum(Calendar.DAY_OF_MONTH);

        spinnerSDDay.setAdapter(createNumberAdapter(1, noDays));
        spinnerSDMonth.setAdapter(createNumberAdapter(1, 12));
        spinnerSDYear.setAdapter(createNumberAdapter(2020, cal.get(Calendar.YEAR)));

        spinnerEDDay.setAdapter(createNumberAdapter(1, noDays));
        spinnerEDMonth.setAdapter(createNumberAdapter(1, 12));
        spinnerEDYear.setAdapter(createNumberAdapter(2020, cal.get(Calendar.YEAR)));

        spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int month = 1;
                int year = 2021;
                Spinner spinnerDay = spinnerSDDay;

                if (parent.getId() == R.id.spinnerFragmentReportsSDMonth) {
                    month = Integer.parseInt(spinnerSDMonth.getAdapter().getItem(position).toString());
                    year = Integer.parseInt(spinnerSDYear.getSelectedItem().toString());
                } else if (parent.getId() == R.id.spinnerFragmentReportsSDYear) {
                    month = Integer.parseInt(spinnerSDMonth.getSelectedItem().toString());
                    year = Integer.parseInt(spinnerSDYear.getAdapter().getItem(position).toString());
                } else if (parent.getId() == R.id.spinnerFragmentReportsEDMonth) {
                    month = Integer.parseInt(spinnerEDMonth.getAdapter().getItem(position).toString());
                    year = Integer.parseInt(spinnerEDYear.getSelectedItem().toString());
                    spinnerDay = spinnerEDDay;
                } else if (parent.getId() == R.id.spinnerFragmentReportsEDYear) {
                    month = Integer.parseInt(spinnerEDMonth.getSelectedItem().toString());
                    year = Integer.parseInt(spinnerEDYear.getAdapter().getItem(position).toString());
                    spinnerDay = spinnerEDDay;
                }


                updateDaysInMonth(spinnerDay, month, year);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };


        switchStartDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean enabled = isChecked;
                spinnerSDDay.setEnabled(enabled);
                spinnerSDMonth.setEnabled(enabled);
                spinnerSDYear.setEnabled(enabled);

            }
        });

        switchEndDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean enabled = isChecked;
                spinnerEDDay.setEnabled(enabled);
                spinnerEDMonth.setEnabled(enabled);
                spinnerEDYear.setEnabled(enabled);


            }
        });

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        AppLog.getInstance().print(App.TAG, "FragmentReport started,");

        //The user could be answering questions before navigating here, so pause
        //the questioning.
        Questioner.getInstance().pause();

        switchGetAll.setChecked(false);
        switchStartDate.setChecked(true);
        switchEndDate.setChecked(true);

        //Disable events
        spinnerSDMonth.setOnItemSelectedListener(null);
        spinnerSDYear.setOnItemSelectedListener(null);
        spinnerEDMonth.setOnItemSelectedListener(null);
        spinnerEDYear.setOnItemSelectedListener(null);

        Date now = new Date();
        Date yesterday = new Date(now.getTime() - MILLISECONDS_PER_DAY);

        Calendar cal = Calendar.getInstance();
        cal.setTime(yesterday);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        selectSpinnerItem(spinnerSDDay, Integer.toString(day));
        selectSpinnerItem(spinnerSDMonth, Integer.toString(month));
        selectSpinnerItem(spinnerSDYear, Integer.toString(year));

        cal.setTime(now);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);

        selectSpinnerItem(spinnerEDDay, Integer.toString(day));
        selectSpinnerItem(spinnerEDMonth, Integer.toString(month));
        selectSpinnerItem(spinnerEDYear, Integer.toString(year));

        spinnerSDMonth.setOnItemSelectedListener(spinnerListener);
        spinnerSDYear.setOnItemSelectedListener(spinnerListener);
        spinnerEDMonth.setOnItemSelectedListener(spinnerListener);
        spinnerEDYear.setOnItemSelectedListener(spinnerListener);


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentReportsEmail) {
            email();
            logReportEvent();
        }
         else if (v.getId() == R.id.buttonFragmentReportsExport) {
            export();
            logReportEvent();
        }
     }

    private void export() {

        try {

            if (!validateInputs()) return;

            btnExport.setEnabled(false);
            final String filename = "report.csv";
            Date sDate = null;
            Date eDate = null;
            DateFormaterEntry dateFormaterEntry = new DateFormaterEntry();
            if (switchStartDate.isChecked()) {
                String strsDate = String.format("%s/%s/%s", spinnerSDDay.getSelectedItem().toString(),
                        spinnerSDMonth.getSelectedItem().toString(),
                        spinnerSDYear.getSelectedItem().toString());

                sDate = dateFormaterEntry.parse(strsDate);
            }

            if (switchEndDate.isChecked()) {
                String streDate = String.format("%s/%s/%s", spinnerEDDay.getSelectedItem().toString(),
                        spinnerEDMonth.getSelectedItem().toString(),
                        spinnerEDYear.getSelectedItem().toString());

                eDate = dateFormaterEntry.parse(streDate);

            }


            viewModel.runReport(new ParameterReport(null, null, switchGetAll.isChecked(), sDate, eDate),
                    filename, new Runnable() {
                        @Override
                        public void run() {
                            //Prompt user for save location.
                            String newFileName = FileName.get("reponses");
                            activityResultLauncher.launch(newFileName);

                        }
                    });

        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }

    }

    private void logReportEvent() {

        try {

            if (!validateInputs()) return;

            Date sDate = null;
            Date eDate = null;
            DateFormaterEntry dateFormaterEntry = new DateFormaterEntry();
            if (switchStartDate.isChecked()) {
                String strsDate = String.format("%s/%s/%s", spinnerSDDay.getSelectedItem().toString(),
                        spinnerSDMonth.getSelectedItem().toString(),
                        spinnerSDYear.getSelectedItem().toString());

                sDate = dateFormaterEntry.parse(strsDate);
            }

            if (switchEndDate.isChecked()) {
                String streDate = String.format("%s/%s/%s", spinnerEDDay.getSelectedItem().toString(),
                        spinnerEDMonth.getSelectedItem().toString(),
                        spinnerEDYear.getSelectedItem().toString());

                eDate = dateFormaterEntry.parse(streDate);

            }
            String datesReport = "Start Date : " + sDate + "\n" + "End Date : " + eDate;
            AppLog.getInstance().reportDatesEvent(Session.getInstance().getUser().getFullName(), (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())), ActionsEnum.EVENT_SUPERVISOR_ENTER_PIN_ACTION.name(), ResultEnum.RESULT_SUCCESS.name(), datesReport);
        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }

    }

    private void email() {
        try {

            Data data = new Data.Builder().putBoolean("update_exported", false).build();

            WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(WorkerEmailResponse.class)
                    .setInputData(data)
                    .build();

            WorkManager workManager = WorkManager.getInstance(this.getContext());
            workManager.enqueue(myWorkRequest);

            DialogMessage dialogMessage = new DialogMessage("Responses emailed. Please check your email in a few minutes before resending.",
                    "ok");
            dialogMessage.show(getParentFragmentManager(), "dialog_email_message");


        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
            //Log.e(App.TAG, ex.getMessage());
            //ex.printStackTrace();
        }
    }

    private ArrayAdapter<String> createNumberAdapter(int start, int end) {
        ArrayList<String> numbers = new ArrayList<String>();
        for (int i = start; i <= end; i++) numbers.add(Integer.toString(i));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this.getActivity(), android.R.layout.simple_spinner_item, numbers);

        return adapter;

    }

    private boolean validateInputs() {
        try {
            if (switchStartDate.isChecked() && switchEndDate.isChecked()) {
                String strsDate = String.format("%s/%s/%s", spinnerSDDay.getSelectedItem().toString(),
                        spinnerSDMonth.getSelectedItem().toString(),
                        spinnerSDYear.getSelectedItem().toString());

                DateFormaterEntry dateFormaterEntry = new DateFormaterEntry();

                Date sDate = dateFormaterEntry.parse(strsDate);

                String streDate = String.format("%s/%s/%s", spinnerEDDay.getSelectedItem().toString(),
                        spinnerEDMonth.getSelectedItem().toString(),
                        spinnerEDYear.getSelectedItem().toString());

                Date eDate = dateFormaterEntry.parse(streDate);

                if (eDate.getTime() < sDate.getTime()) {
                    DialogMessage dialogMessage = new DialogMessage(getString(R.string.validation_end_date_after_start_date),
                            getString(android.R.string.ok));
                    dialogMessage.show(getParentFragmentManager(), "dialog_message_date_validation");

                    return false;
                }
            }

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

        return true;
    }

    private void updateDaysInMonth(final Spinner sDay, int month, int year) {

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            int noDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>) sDay.getAdapter();
            arrayAdapter.clear();
            for (int i = 1; i <= noDays; i++) arrayAdapter.add(Integer.toString(i));
            arrayAdapter.notifyDataSetChanged();

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    private void selectSpinnerItem(final Spinner spinner, String val) {
        for (int idx = 0; idx < spinner.getAdapter().getCount(); idx++) {
            if (spinner.getAdapter().getItem(idx).toString().equals(val)) {
                spinner.setSelection(idx);
                break;
            }
        }
    }

}