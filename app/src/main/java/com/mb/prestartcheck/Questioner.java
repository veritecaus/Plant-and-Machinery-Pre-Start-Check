package com.mb.prestartcheck;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mb.prestartcheck.console.FragmentSettingsBypassReportingOptions;
import com.mb.prestartcheck.data.PrestartCheckDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Questioner {


    public enum  QuestionState
    {
        Unknown,
        Question,
        Finished,
        Info,
        Error,
        Error_Sections_Empty,
        Error_Current_Question_Missing,
        Error_Current_Section_Missing,
        Paused
    };

    public class QuestionerState {
        Question currentQuestion;
        Section currentSection;
        int currentProgress;
        int totalProgress;
        User user;
        Responses responses = new Responses();
        private int savedNavDestination;
        private QuestionState state;
        private int lastFragmentId;
        private Bundle lastFragmentBundle;

        public Question getCurrentQuestion() {
            return currentQuestion;
        }
        public Section getCurrentSection() {  return currentSection; }
        public void setCurrentQuestion(Question e) { this.currentQuestion = e;}
        public void setCurrentSection(Section e) { this.currentSection = e;}

        public User getUser() { return user;}
        public void setUser(User e) { this.user = e;}

        public  int getCurrentProgress() { return currentProgress;}
        public  void setCurrentProgress(int val) { this.currentProgress = val;}

        public  int getTotalProgress() { return totalProgress;}
        public  void setTotalProgress(int val) { this.totalProgress = val;}

        public Responses getResponses() { return this.responses;}

        public int getSavedNavDestination() { return this.savedNavDestination;}
        public void setSavedNavDestination(int id) { this.savedNavDestination = id;}

        public QuestionState getState() { return this.state;}
        public void setState(QuestionState state) { this.state = state;}

        public  int getLastFragmentId() { return this.lastFragmentId;}
        public void setLastFragmentId(int lastFragmentId) { this.lastFragmentId = lastFragmentId;}

        public Bundle getLastFragmentBundle() { return this.lastFragmentBundle;}
        public void setLastFragmentBundle(Bundle bundle) { this.lastFragmentBundle = bundle;}
    }

    public static final Predicate<Response> pred_unexpected_responses = new Predicate<Response>() {
        @Override
        public boolean test(Response response) {
            if (response == null) return false;

            if (response.getQuestion() == null) return false;

            //Ignore critical questions because the supervisor has already accepted liability.
            if (response.getQuestion().getIsCritical()) return false;

            return !response.getQuestion().isExpectedResponse(response);
        }
    };


    public interface OnBeforeQuestionChangeHandler
    {
        public void beforeQuestionChange(Question prevQuestion, Question nextQuestion);
    }

    public interface BeforeQuestionerStartHandlerListener
    {
        public void onBeforeQuestionerStart();
    }

    public interface QuestionerStateListener
    {
        //TODO: Remove parameter openInterlock.
        void onQuestionerBeforeRestart(final Question lastQuestion, boolean openInterlock);
        void onQuestionerBeforePause(final Question lastQuestion);
    }


    static Questioner instance;
    QuestionerState state = new QuestionerState();
    private  OnBeforeQuestionChangeHandler handlerBeforeQuestionChange;
    private BeforeQuestionerStartHandlerListener listenerOnBeforeQuestionerStart;
    private QuestionerStateListener questionerStateListener;

    public void setHandlerBeforeQuestionChange(OnBeforeQuestionChangeHandler handler) { handlerBeforeQuestionChange = handler;}
    public  void setListenerOnBeforeQuestionerStart(BeforeQuestionerStartHandlerListener l) { this.listenerOnBeforeQuestionerStart = l;}

    public Questioner()
    {

    }

    public QuestionState moveNext()
    {
        if (Sections.getInstance().count(Section.pred_section_enabled) == 0 ) return QuestionState.Finished;

        if (this.state.currentSection == null) {

            if (listenerOnBeforeQuestionerStart != null) listenerOnBeforeQuestionerStart.onBeforeQuestionerStart();
            /*************************************************
             * Order sections for question.
             *************************************************/
            Sections.getInstance().assignDisplayOrder(Section.pred_section_enabled);
            /*************************************************
             * Start of questioning.
             *************************************************/
            onStartQuestioning();
        }

        return trasverseQuestions(this.state.currentSection.getDisplaySequence());
    }

    private QuestionState trasverseQuestions(int sectionSequence)
    {
        try
        {

            Sections sections = Sections.getInstance();

            if (sectionSequence > sections.getMaxSequence(Section.pred_section_enabled))
            {

                return QuestionState.Finished;
            }

            Section section= sections.find(Section.pred_section_display_sequence, sectionSequence);

            if (!this.state.currentSection.equals(section)) {
                this.state.currentSection = section;
                this.state.setTotalProgress(this.state.currentSection.count(Question.pred_question_enabled));
            }

            if (this.state.getCurrentQuestion() == null ) {

                //***********************
                // During the setup of the section, questions that are enabled will be
                //  given a sequential order from the question sequence unless the section
                // is randomized; and, in that case of randomization, a vector of random
                // indices is used for ordering.
                //***********************
                section.setup();
                this.state.setCurrentProgress(0);

                if (section.getShowCoverPage()) {
                    this.state.setCurrentQuestion(QuestionCoverPage.getInstance());
                    return QuestionState.Info;
                }

            }

            int nextQuestionSeq = getNextQuestionIndex(this.state.getCurrentSection(),
                                                       this.state.currentQuestion == QuestionCoverPage.getInstance() ||  this.state.currentQuestion == null ? 0 :
                                                        this.state.currentQuestion.getQuestioningOrder());

            Question question = null;


            if (nextQuestionSeq > Section.getMaxSequence(section, Question.pred_question_enabled)) {
                onQuestionBeforeChanged(this.state.getCurrentQuestion(), question);

                //End of this section ,  check if there were any unexpected answers before
                //preceeding to the next section.

                this.getQuestionerState().setCurrentQuestion(null);
                //Find the next section by sequence.
                int nextSequence  = sections.nextSequence(Section.pred_section_enabled, this.state.getCurrentSection().getDisplaySequence());

                return trasverseQuestions(nextSequence);
            }
            else {
                question =  this.state.getCurrentSection().find(Question.pred_question_enabled_sequence, nextQuestionSeq);
                onQuestionBeforeChanged(this.state.getCurrentQuestion(), question);

                this.state.setCurrentQuestion(question);
                this.state.setCurrentProgress(this.state.getCurrentProgress()+1);
            }


        }
        catch(Exception ex)
        {
            Log.e(App.TAG, ex.getMessage());
            return QuestionState.Error;
        }
        return QuestionState.Question;

    }


    private int getNextQuestionIndex(Section s, int currentSeq)
    {
        //Find the highest sequence number after currentSeq.
        /*
        List<Question> tmp = s.find(Question.pred_question_enabled);
        Collections.sort(tmp, new ComparerQuestioningOrder());

        for(Question question : tmp ) {
            if (question.getQuestioningOrder() > currentSeq)
                return s.indexOf(question);
        }

         */

        return ++currentSeq;
    }

    private void onStartQuestioning()
    {
        this.state.currentSection = Sections.getInstance().first(Section.pred_section_enabled);
        this.state.setTotalProgress(this.state.currentSection.count(Question.pred_question_enabled));
        this.state.getResponses().clear();
    }

    private void onQuestionBeforeChanged(@Nullable final Question prevQuestion, @Nullable final Question nextQuestion)
    {

        if (prevQuestion !=null && !prevQuestion.equals(QuestionCoverPage.getInstance()) &&  handlerBeforeQuestionChange != null) handlerBeforeQuestionChange.beforeQuestionChange(prevQuestion, nextQuestion);

        if (nextQuestion != null && !nextQuestion.equals(QuestionCoverPage.getInstance())) {
            //Create a new response for the next question.
            Response nextResponse = this.state.getResponses().find( nextQuestion);
            if (nextResponse == null) {
                this.state.responses.add(new Response(nextQuestion, this.state.getCurrentSection()));
            }
        }


    }

    public QuestionerState getQuestionerState() { return state;}

    public static Questioner getInstance() {
        if (instance == null)
        {
            instance = new Questioner();
            //The app is always interested in what the questioner is doing.
            instance.questionerStateListener = App.getInstance();
        }
        return instance;
    }

    public Response getCurrentResponse()
    {
        return this.getQuestionerState().getResponses().find(getQuestionerState().getCurrentQuestion());
    }

    public int count(Predicate<Response> pred, Predicate<Section> predSection, Predicate<Question> predQuestion)
    {
        int cnt = 0;
        for(int idx = 0 ; idx < Sections.getInstance().getSize(); idx++)
        {
            Section section = Sections.getInstance().getAt(idx);
            if (predSection.test(section)) {
                for (int jdx = 0; jdx < section.getSize(); jdx++) {
                    Question question = section.getAt(jdx);

                    if (predQuestion.test(question))
                    {
                        Response response = state.getResponses().find(question);
                        cnt += pred.test(response) ? 1 : 0;
                    }
                }
            }
        }


        return cnt;
    }

    public List<Response> getUnexpectedResponses()
    {
        ArrayList<Response> responses = new ArrayList<Response>();
        for(int idx = 0 ; idx < Sections.getInstance().getSize(); idx++)
        {
            Section section = Sections.getInstance().getAt(idx);
            for (int jdx = 0 ; jdx < section.getSize(); jdx++)
            {
                Question question = section.getAt(jdx);
                Response response = state.getResponses().find(question);
                if (pred_unexpected_responses.test(response)) responses.add(response);
            }
        }

        return  responses;
    }

    public  void restart(boolean openInterlockDevice)
    {
        if (questionerStateListener != null) questionerStateListener.onQuestionerBeforeRestart(
                this.state == null ? null : this.state.getCurrentQuestion(),  openInterlockDevice);
/*
        if (openInterlockDevice) {
            if (this.state != null && this.state.getCurrentQuestion() != null &&
                    this.state.getCurrentQuestion().getAllowMachineOperation())
                ProxyInterlockDevice.openInterlockDevice();
        }
*/
        this.state.setCurrentQuestion(null);
        this.state.setCurrentSection(null);
        this.state.setState(QuestionState.Unknown);
    }

    public  void pause()
    {
        if (questionerStateListener != null)
            questionerStateListener.onQuestionerBeforePause(this.state == null ? null : this.state.getCurrentQuestion());
/*
        if (this.state != null && this.state.getCurrentQuestion() != null &&
                this.state.getCurrentQuestion().getAllowMachineOperation())
            ProxyInterlockDevice.openInterlockDevice();
*/
        if (this.state.getCurrentSection() != null)
            this.state.setState(QuestionState.Paused);
    }

}
