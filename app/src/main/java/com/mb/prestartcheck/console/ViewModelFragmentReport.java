package com.mb.prestartcheck.console;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.FileName;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ParameterReport;
import com.mb.prestartcheck.ReportLine;
import com.mb.prestartcheck.Reporter;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class ViewModelFragmentReport extends AndroidViewModel {


    public ViewModelFragmentReport(@NotNull Application application) {
        super(application);
    }
    // TODO: Implement the ViewModel

    public void runReport(ParameterReport param, String filepath, Runnable onComplete)
    {
         Reporter reporter = new Reporter(Machine.getInstance(), getApplication().getApplicationContext(), new Reporter.ReporterListener() {
            @Override
            public void onPublishAllResponses(List<ReportLine> report, String headerLine) {
                //Write to file.

                try {
                    FileOutputStream fos = getApplication().getApplicationContext().openFileOutput(filepath, 0);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);

                    //Output header line
                    outputStreamWriter.write(headerLine);
                    outputStreamWriter.write("\n");

                    for(ReportLine reportLine : report)
                    {
                        String line= ReportLine.toCSV(reportLine);
                        outputStreamWriter.write(line);
                    }
                    outputStreamWriter.close();
                    fos.close();

                    onComplete.run();
                }
                catch (Exception ex)
                {
                    Log.e(App.TAG, ex.getMessage());
                }

            }
        });

        reporter.publishResponses(param.isGetAll(), param.getFirstName(), param.getLastName(), param.getStartDate(),
                                    param.getEndDate());

    }

}