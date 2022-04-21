package com.mb.prestartcheck.console;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionAcknowledgement;
import com.mb.prestartcheck.QuestionCoverPage;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.QuestionYesNo;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Response;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.data.DaoResponse;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableResponse;

import java.util.Date;

/**
 * Decendants :
 * ViewModelFragmentHome,
 * ViewModelSectionCover,
 * ViewModelFragmentQuestionYesNo,
 * ViewModelFragmentQuestionMultipleChoice,
 * ViewModelFragmentQuestionOptions,
 * ViewModelFragmentQuestionYesNo
 */
public class ViewModelQuestionNavigator extends AndroidViewModel
 {

    protected Bundle navBundle = new Bundle();
    private int nextQuestionId;

    protected final Question question;
    protected final Section section;

    //Not all Questions will show the section info button
     //on the navigation bar (  home and section cover page).
     //Explicitly set this property to true , to display the
     //section info button.
    protected  boolean showSectionInfo = false;

     /**
      * Default to not showing the section infornmation button.
      * @param app Reference to an application instance.
      * @param question The question this view model is displaying.
      * @param section The section containing the question.
      */
     protected ViewModelQuestionNavigator(Application app ,@Nullable final Question question,
                                          @Nullable final Section section)
     {
         this(app, question, section, false);
     }

     /**
      * Allow descendants to set base properties. Should not
      * create this class. Reference to a question and section
      * is required.
      * @param app Reference to an application instance.
      * @param question The question this view model is displaying.
      * @param section The section containing the question.
        @param showSectionInfo True to display the section info button.
      */
    protected ViewModelQuestionNavigator(Application app ,@Nullable final Question question,
                                         @Nullable final Section section,
                                         boolean showSectionInfo)
    {
        super(app);
        this.question = question;
        this.section = section;
        this.showSectionInfo = showSectionInfo;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public Bundle getNavBundle() {
        return navBundle;
    }

    //These properties were in some child classes. Now,
    //it has moved here.
    public  Question getQuestion() { return this.question;}
    public  Section getSection() { return this.section;}
    public  boolean getShowSectionInfo() { return this.showSectionInfo;}

     public Questioner.QuestionState moveNext()
    {
        /**********************************************************************
         *  Use the current state of questioner to determine what to display.
         * The start condition is nulls in the current states.
         **********************************************************************/
        Questioner questioner = Questioner.getInstance();
        navBundle.clear();
        Questioner.QuestionState retstate =questioner.moveNext();
        //Save the returned state inside the questionair.
        questioner.getQuestionerState().setState(retstate);

        if (retstate == Questioner.QuestionState.Question || retstate == Questioner.QuestionState.Info)
        {
            Section section = questioner.getQuestionerState().getCurrentSection();


            if (section == null)
            {
                //TODO: Handle the scenario when the next section is null:
                //something when wrong if we can't find the next question
                //to ask.
                Log.e(App.TAG, "The next section found was null!.");
                return Questioner.QuestionState.Error;
            }

            navBundle.putLong("section_id", section.getId());
            navBundle.putString("section_title", section.getTitle());

            Question question = questioner.getQuestionerState().getCurrentQuestion();

            if (question == null)
            {
                Log.e(App.TAG, String.format("Could not get the next question in  Section %s.", section.getTitle()));
                return Questioner.QuestionState.Error;
            }

            navBundle.putLong("question_id",  question.getId());

            nextQuestionId =  -1;

            if (question.getClass() == QuestionCoverPage.class)
            {
                nextQuestionId = R.id.nav_section_cover;
                Log.i(App.TAG, "Navigating to section cover page");
            }
            if (question.getClass() == QuestionYesNo.class)
            {
                nextQuestionId = R.id.nav_question_yes_no;
                Log.i(App.TAG, "Navigating to a yes/no question.");

            }
            else if (question.getClass() == QuestionMultipleChoice.class)
            {
                nextQuestionId = R.id.nav_question_multiple_choice;
                Log.i(App.TAG, "Navigating to a multiple choice question.");
            }
            else if (question.getClass() == QuestionAcknowledgement.class)
            {
                nextQuestionId = R.id.nav_question_ack;
                Log.i(App.TAG, "Navigating to an acknowledgement question.");
            }
            else if (question.getClass() == QuestionOptions.class)
            {
                nextQuestionId = R.id.nav_question_options;
                Log.i(App.TAG, "Navigating to an options question.");
            }


        }



        return retstate;

    }

    public void saveResponse(Response response)
    {
        PrestartCheckDatabase db = PrestartCheckDatabase.getDatabase(this.getApplication().getApplicationContext());
        db.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                DaoResponse daoResponse = db.getDaoResponse();
                Date now = new Date();
                response.setUpdatedDateTime(now);
                TableResponse tableResponse = response.toTableResponse(Questioner.getInstance().getQuestionerState().getUser(),
                        Machine.getInstance());

                long id = daoResponse.insert(tableResponse);
                response.setId(id);
            }
        });
    }

    public void closeInterlockDevice()
    {

        Question question = Questioner.getInstance().getQuestionerState().getCurrentQuestion();

        //Default ot 5 mins.
        int timeout = question != null &&  question.getTimeout() > 0 ? question.getTimeout() : 5;
        ProxyInterlockDevice.closeInterlockDevice(timeout);
    }

    public void openInterlockDevice()
    {
        ProxyInterlockDevice.openInterlockDevice();
    }

     /**
      * Set the question's "opCount" property to zero.
      */
    public void clearOpCount()
    {
        getQuestion().setOpCounter(0);
    }

     /**
      * For the current question , check the number of times the operator has runned
      * the machine.
      * @return Return true if the operator has operated the machine more than ... times.
      */
    public boolean hasExceededOpCount() {
        return getQuestion().getOpCounter() > Question.MAX_MACHINE_OP_COUNT;
    }


}
