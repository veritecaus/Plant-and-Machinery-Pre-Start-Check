package com.mb.prestartcheck.console;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.ProxyInterlockDevice;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.QuestionTimeoutTaskTimer;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Response;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Settings;

import java.util.Timer;

/**
 * Common functionality for all question type fragments:
 * assist the questioning process navigate between
 * questions and display machine operating popup.
 **/
public abstract class FragmentQuestion extends Fragment {


    protected ViewModelQuestionNavigator viewModel;
    //protected DialogMachingOperation dialogMachineOp = new DialogMachingOperation(this);
    private Timer timer;
    private QuestionTimeoutTaskTimer task;
    public final static String DIALOG_TAG_MACHINE_OP = "machine_operation";
    public final static String DIALOG_TAG_SUPERVISOR_PIN = "supervisor_pin";
    public final static String DIALOG_TAG_SUPERVISOR_LOGON = "supervisor_login";


    /**
     * Called BEFORE question changes.
     *
     * @param viewModelNav
     */
    protected void nextQuestion(ViewModelQuestionNavigator viewModelNav) {
        try {

            //Clear timers on machine operation.
            //TODO : Move this to view model.
            Question currentQuestion = Questioner.getInstance().getQuestionerState().getCurrentQuestion();
            if (currentQuestion != null && Question.isQuestionRequireResponse(currentQuestion) && currentQuestion.getAllowMachineOperation()) {
                dismissMachineLockTimeout();
                final ViewModelQuestionNavigator viewModelQuestionNavigator =
                        (ViewModelQuestionNavigator) FragmentQuestion.this.viewModel;
                viewModelQuestionNavigator.openInterlockDevice();

                //Previous question counted how many times the machine was runnning.
                //Reset this value when moving away from the question. The next
                //time the question is asked, the count will start at zero.
                viewModelQuestionNavigator.clearOpCount();

            }

            //Check expected answer ?
            if (currentQuestion != null && currentQuestion.getIsCritical()
                    && !Questioner.getInstance().getCurrentResponse().getAnswerReviewed() &&
                    !currentQuestion.isExpectedResponse(Questioner.getInstance().getCurrentResponse())) {
                DialogQuestion dialogQuestion = new DialogQuestion("Your supervisor will need to approve your response. Press proceed to continue or cancel to change your answer.",
                        "Proceed", "", this);

                dialogQuestion.show(getActivity().getSupportFragmentManager(), DIALOG_TAG_SUPERVISOR_LOGON);
                return;
            }

            Questioner.QuestionState result = viewModelNav.moveNext();
            if (result == Questioner.QuestionState.Question || result == Questioner.QuestionState.Info) {
                final NavController navController = NavHostFragment.findNavController(this);
                //Save destination as the last fragment visited (in case the user navigates away).
                Questioner.getInstance().getQuestionerState().setLastFragmentId(viewModelNav.getNextQuestionId());
                Questioner.getInstance().getQuestionerState().setLastFragmentBundle(viewModelNav.getNavBundle());

                navController.navigate(viewModelNav.getNextQuestionId(), viewModelNav.navBundle);
            } else if (result == Questioner.QuestionState.Finished) {

                int nounexpectedresp = Questioner.getInstance().count(Questioner.pred_unexpected_responses,
                        Section.pred_section_enabled, Question.pred_question_enabled);

                final NavController navController = NavHostFragment.findNavController(this);
                int fragmentId = nounexpectedresp > 0 ? R.id.nav_summary : R.id.nav_logout;

                //Save destination as the last fragment visited (in case the user navigates away).
                Questioner.getInstance().getQuestionerState().setLastFragmentId(fragmentId);
                Questioner.getInstance().getQuestionerState().setLastFragmentBundle(viewModelNav.getNavBundle());

                navController.navigate(fragmentId);
            }
        } catch (Exception e) {
            Log.e(App.TAG, e.getMessage());
        }
    }

    protected void currentQuestion() {
        try {

            if (Questioner.getInstance().getQuestionerState().getLastFragmentId() > 0) {
                final NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(Questioner.getInstance().getQuestionerState().getLastFragmentId(),
                        Questioner.getInstance().getQuestionerState().getLastFragmentBundle());
            }

        } catch (Exception e) {
            AppLog.getInstance().print(e.getMessage());
        }
    }


    /**
     * Display a small small message at the bottom of the screen that shows how
     * many questions are left to answer.
     *
     * @param viewText Format of the text to display.
     */
    protected void setProgress(TextView viewText) {
        viewText.setText(String.format("Question %d of %d", Questioner.getInstance().getQuestionerState().getCurrentProgress(),
                Questioner.getInstance().getQuestionerState().getTotalProgress()));
    }


    /**
     * Signal that this fragment and it's decendants
     * could have  menus -- depending on it's view model.
     *
     * @return
     */

    public void setHasMenu() {
        try {
            //Indicate that this fragment has menu items, only if
            //explicitly set in the view model.
            if (((ViewModelQuestionNavigator) this.viewModel).getShowSectionInfo())
                setHasOptionsMenu(true);
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        DialogSupervisorLogin dialogSupervisorLogin = new DialogSupervisorLogin(this);
//        dialogSupervisorLogin.show(getActivity().getSupportFragmentManager(), DIALOG_TAG_SUPERVISOR_PIN);
        try {

            /************************************************************
             * A question can navigate back to itself if a supervisor was
             * asked to approve a response. So, in this case, we don't
             * want to reload images or open the interlock device.
             * However, we do want to record the response when the
             * question has traversed to the next question.
             ************************************************************/
            boolean supervisorBypassed = getArguments() != null &&
                    getArguments().containsKey("supervisor_bypass") ?
                    getArguments().getBoolean("supervisor_bypass", false) : false;

            if (!supervisorBypassed) {
                //Check if the machine will be operating.
                Question question = Questioner.getInstance().getQuestionerState().getCurrentQuestion();
                //Load images if it has one
                loadQuestionImages(question);

                if (question != null && question.getAllowMachineOperation() && canOperateMachine())
                    alertOperatorOfMachineOperation();

            } else
                AppLog.getInstance().print("Supervisor has accepted the response. Moving to the next question.");

            Questioner.getInstance().setListenerOnBeforeQuestionerStart(new Questioner.BeforeQuestionerStartHandlerListener() {
                @Override
                public void onBeforeQuestionerStart() {
                    AppLog.getInstance().print("Questioner has started questioning. Opening, if interlock device is closed.");
                    ViewModelQuestionNavigator viewModelQuestionNavigator = (ViewModelQuestionNavigator) FragmentQuestion.this.viewModel;
                //The interlock device could be off. In this scenario,
                //first check if there is a connection to the interlock device.
                //If there is no connection to the interlock device, then reconnect
                //to it.

                //Get the IP of the interlock device and the FINS node number from the application settings.
                String addr = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_ADDRESS);
                String nodeNo = App.getInstance().getSettings().get(Settings.INTERLOCK_DEVICE_FIN_NODE_ADDRESS);
                ProxyInterlockDevice.reconnect(addr, nodeNo, null);

                //At thie stage, the interlock device is connected or eas reconnected.
                ProxyInterlockDevice.openInterlockDevice();

                }
            });

            Questioner.getInstance().setHandlerBeforeQuestionChange(new Questioner.OnBeforeQuestionChangeHandler() {
                @Override
                public void beforeQuestionChange(Question prevQuestion, Question nextQuestion) {


                    //Save response to the database.
                    Response response = Questioner.getInstance().getQuestionerState().getResponses().find(prevQuestion);

                    ViewModelQuestionNavigator viewModelQuestionNavigator = (ViewModelQuestionNavigator) FragmentQuestion.this.viewModel;
                    viewModelQuestionNavigator.saveResponse(response);

                }
            });


        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Pause recording responses.
            Questioner.getInstance().setHandlerBeforeQuestionChange(null);
            Questioner.getInstance().setListenerOnBeforeQuestionerStart(null);
            dismissMachineLockTimeout();
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    /**
     * Add a button to display the section information for this question.
     * Menus common to all question is located in the "menu_question.xml" resource.
     * When the fragment is created, override the "onCreateOptionsMenu" method
     * to display menu items from the "menu_question.xml" resource.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //Using the  inflater argument,  insert the question menu items into "menu".
        inflater.inflate(R.menu.menu_question, menu);
    }

    /**
     * User has tapped on section information button on the navigation
     * bar.
     *
     * @param item the "action_menu_question_view_section_desc" menu instance.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_menu_question_view_section_desc) {
            //Get the current section.
            Section section = ((ViewModelQuestionNavigator) this.viewModel).getSection();

            /**
             * Open a pop up fragment that displays infornmation
             * about the current section ( section description).
             * Don't add to the navigation graph, instead,
             * display a dialog. Use the AlertManager class.
             */

            if (section != null) AlertManager.showDialog(getContext(), section.getDescription(),
                    R.string.button_label_close);

        }

        return super.onOptionsItemSelected(item);
    }


    public void dismissedDialog() {
        alertOperatorOfMachineOperation();
    }

    /**
     * The user is prompted with a message stating that the machine is running
     * for this question. From the MainActivity, this method is called  when the
     * user dismisses the message by pressing "OK".
     *
     * @return
     */
    public void operateMachine() {

        try {
            final ViewModelQuestionNavigator viewModelQuestionNavigator =
                    (ViewModelQuestionNavigator) FragmentQuestion.this.viewModel;

            //TODO: Check how many times the machine was running when this
            //for this particular question.

            viewModelQuestionNavigator.closeInterlockDevice();
            startMachineLockTimeout();

        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }
    }

    private void alertOperatorOfMachineOperation() {
        if (viewModel.question.getOpCounter() < Question.MAX_MACHINE_OP_COUNT) {

            //Display warning.
            viewModel.question.setOpCounter(viewModel.question.getOpCounter() + 1);
            DialogMachingOperation dialogMachingOperation = new DialogMachingOperation(this);
            dialogMachingOperation.show(this.getParentFragmentManager(), DIALOG_TAG_MACHINE_OP);
        /*
        if (!this.dialogMachineOp.isVisible()) {
            this.dialogMachineOp.show(this.getParentFragmentManager(), DIALOG_TAG_MACHINE_OP);
        }
         */
        } else {
            //Display enter supervisor pin pop up.
            DialogSupervisorLogin dialogSupervisorLogin = new DialogSupervisorLogin(this);
            dialogSupervisorLogin.show(getActivity().getSupportFragmentManager(), DIALOG_TAG_SUPERVISOR_PIN);
            viewModel.question.setOpCounter(0);
        }
    }

    private void startMachineLockTimeout() {
        /**
         * Disimiss runing timers.
         */
        dismissMachineLockTimeout();

        Question question = Questioner.getInstance().getQuestionerState().getCurrentQuestion();
        //default to 30 seconds
        int timeout = question.getTimeout() <= 0 ? 30 : question.getTimeout() * 60;

        /**
         * Close the interlock device.  Add a timer task using the an instance of the TimerTask
         * class. The timer class spawns a thread then pauses it for an amount of time specified
         * in the current question. If the timer is left to complete, then  the interlock device
         * is opened and the process repeats ( up to three times) .
         */

        this.timer = new Timer("machine_lock_timer");

        //Once the timer has ellapsed, it will notify  it's listeners.
        //If the timer was allowed to complete, the anonymous function below
        //is triggered and the interlock device is opened ( machine stopped).
        this.task = new QuestionTimeoutTaskTimer(new QuestionTimeoutTaskTimer.QuestionTimeoutTaskListener() {
            @Override
            public void questionTimeoutCompleted() {

                final ViewModelQuestionNavigator viewModelQuestionNavigator =
                        (ViewModelQuestionNavigator) FragmentQuestion.this.viewModel;

                viewModelQuestionNavigator.openInterlockDevice();

                //Time out occured. Operator did not answer the question, so
                //repeat the warning message and operate the machine again.
                //~the operator is allowed three notifications before
                //the machine is permanently stopped and a supervisor
                //is required to restart it.

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        alertOperatorOfMachineOperation();
                    }
                });

            }

            @Override
            public void questionTimeoutCanceled() {

            }
        }, timeout);

        //Start the timer here.
        this.timer.schedule(this.task, 5);

    }

    /**
     * Cancel the running timer task .
     * Clear and purge the timer queue .
     * This method is called for these scenarios:
     * i)Before the next question and if the last question required machine operation.
     * ii)If there was no answer to current question and the timer ellapsed.
     *
     * @return
     */
    public void dismissMachineLockTimeout() {
        try {
            if (this.task != null) {
                Log.d(App.TAG, "dismissMachineLockTimeout, task was canceled.");
                this.task.setCanceled(true);
                this.task = null;
            }

            if (this.timer != null) {
                this.timer.cancel();
                this.timer.purge();
                Log.d(App.TAG, "dismissMachineLockTimeout, timer was canceled.");
                this.timer = null;
            }


        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }
    }

    public void askForSupervisorLogin() {
        try {

            final NavController navController = NavHostFragment.findNavController(this);
            //Save where we were before navigating away.

            Questioner.getInstance().getQuestionerState().setSavedNavDestination(navController.getCurrentDestination().getId());
            Bundle bundle = new Bundle();
            bundle.putBoolean("show_risk_notice", true);
            navController.navigate(R.id.nav_login_supervisor, bundle);

        } catch (Exception ex) {
            Log.e(App.TAG, ex.getMessage());
        }
    }

    private void loadQuestionImages(final Question question) {
        try {
            if (question == null) return;

            Context ctx = getActivity().getApplicationContext();
            if (QuestionOptions.class.isAssignableFrom(question.getClass()))
                QuestionOptions.loadResources((QuestionOptions) question, ctx);
            else
                QuestionOptions.loadResources(question, ctx);


        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    protected abstract boolean canOperateMachine();


}
