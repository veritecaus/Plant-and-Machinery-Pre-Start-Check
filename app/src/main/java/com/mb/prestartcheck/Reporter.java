package com.mb.prestartcheck;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.mb.prestartcheck.data.DaoResponse;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class Reporter {

    private final Machine machine;
    private final User user;
    private final Context context;
    private ReporterListener listener;

    public interface ReporterListener
    {
        void onPublishAllResponses(List<ReportLine> report, String headerline);
    }

    public Reporter(Machine m, User user)
    {
        this.machine = m;
        this.user = user;
        this.context = null;
    }

    public Reporter(Machine m, Operator op)
    {
        this.machine = m;
        this.user = op;
        this.context = null;
    }

    public Reporter(Machine m,  Context context, ReporterListener listener)
    {
        this.machine = m;
        this.user = null;
        this.context = context;
        this.listener = listener;
    }

    public List<ReportLine> publishQuestioner()
    {

        try
        {
            ArrayList<ReportLine> reportLines = new ArrayList<ReportLine>();

            List<Response> responses = Questioner.getInstance().getUnexpectedResponses();

            Date now = new Date();
            for(Response response : responses)
            {
                Question question = response.getQuestion();
                Section section = question.getParent();
                String opRes= response.getOperatorResponse();
                String exAns = response.getIsNegative() ? question.getExpectedAnswerNeg() :  question.getExpectedAnswer();

                ReportLine reportLine = new ReportLine(machine.getName(),
                        user.getFirstName(),
                        user.getLastName(),
                        now,
                        section.getSequence(),
                        section.getTitle(),
                        question.getNumber(),
                        question.getId(),
                        response.getIsNegative() ? question.getTitleAlternative() : question.getTitle(),
                        response.getIsNegative(),
                        question.getTypeString(),
                        exAns,
                        opRes,
                        question.getAllowMachineOperation(),
                        response.getAnswerReviewed(),
                        response.getClearBy(),
                        response.getClearAt(),
                        Session.getInstance().getUuid());

                        reportLines.add(reportLine);
            }


            return reportLines;
        }
        catch (Exception ex)
        {
            Log.e(App.TAG, ex.getMessage());
        }
        return null;
    }

    public void publishResponses(boolean getAll, @Nullable String fname, @Nullable String lname,
                                 @Nullable Date sDate ,@Nullable Date eDate)
    {
        PrestartCheckDatabase prestartCheckDatabase = PrestartCheckDatabase.getDatabase(context);
        DaoResponse dao =  prestartCheckDatabase.getDaoResponse();

        List<TableResponse> responses = null;

        responses = getAll ?  dao.getAll() : dao.get(fname, lname, sDate, eDate);

        ArrayList<ReportLine> report = new ArrayList<ReportLine>();

        for(TableResponse row : responses)
            report.add(ReportLine.create(row));

        if (listener != null) listener.onPublishAllResponses(report, getHeaderLine());

    }

    public int publishReponsesToFile(Context ctx , File file) throws FileNotFoundException, IOException
    {
        PrestartCheckDatabase db = PrestartCheckDatabase.getDatabase(ctx);
        DaoResponse daoResponse = db.getDaoResponse();

        List<TableResponse> resultset = daoResponse.getAllExported(0);

        FileOutputStream fileOutputStream =  new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

        outputStreamWriter.write(getHeaderLine());
        outputStreamWriter.write("\n");

        for (TableResponse row : resultset) {
            String csv = ReportLine.toCSV(ReportLine.create(row));
            outputStreamWriter.write(csv);
        }

        outputStreamWriter.close();
        fileOutputStream.close();

        return resultset.size();

    }

    public String getHeaderLine()
    {
        return "machine," +
               "first_name," +
               "last_name," +
                "date," +
                "time," +
                "section_number," +
                "section_name," +
                "question_number," +
                "question_id," +
                "Positive/Negative," +
                "question_text," +
                "respone_type," +
                "expected_response," +
                "operator_response," +
                "machine_unlocked," +
                "answer_review," +
                "clear_by," +
                "clear_at";
    }


}
