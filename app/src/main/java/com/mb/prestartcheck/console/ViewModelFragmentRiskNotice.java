package com.mb.prestartcheck.console;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Response;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.Supervisors;

import java.util.Date;

public class ViewModelFragmentRiskNotice extends AndroidViewModel {

    final private Supervisor supervisor;

    public ViewModelFragmentRiskNotice(@NonNull Application application, long supervisorId) {
        super(application);
        this.supervisor = Supervisors.getInstance().find(supervisorId);
    }

    public Question getQuestion()
    {
        return Questioner.getInstance().getQuestionerState().getCurrentQuestion();
    }

    public Section getSection()
    {
        return Questioner.getInstance().getQuestionerState().getCurrentSection();
    }

    public Response getReponse()
    {
        return Questioner.getInstance().getCurrentResponse();
    }

    public  Supervisor getSupervisor()
    {
        return this.supervisor;
    }

    public void manageRisk(boolean accepted)
    {
        getReponse().setReviewed(accepted, getSupervisor());
    }

}
