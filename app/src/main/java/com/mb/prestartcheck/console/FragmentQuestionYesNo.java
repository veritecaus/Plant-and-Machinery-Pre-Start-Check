package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;

import java.util.Random;

public class FragmentQuestionYesNo extends FragmentQuestion implements  View.OnClickListener {

    //ViewModelFragmentQuestionYesNo viewModel;
    private View rootView;
    private boolean isNegativeQuestion;
    private ImageView imageViewQuestion;
    private TextView tvTitle;
    private TextView tvComment;
    private TextView tvStatus;


    /**
     * Return the associated view model.
     * @return Instance of the ViewModelQuestionNavigator class or a decendant.
     */
    private ViewModelFragmentQuestionYesNo getViewModel() {
        return (ViewModelFragmentQuestionYesNo)super.viewModel;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Here, we create the view model class so that the onCreateView override
         * can use it.
         */
        long qid = getArguments().getLong("question_id", -1);
        super.viewModel = new ViewModelFragmentQuestionYesNo(getActivity().getApplication(), qid);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If specified in the view model, add a menu.
        setHasMenu();

        rootView = inflater.inflate(R.layout.fragment_question_yes_no, container, false);
        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       //Keep references to used views inside this Fragment.
        imageViewQuestion = rootView.findViewById(R.id.imageViewFragmentQuestionYesNoPic1);
        tvTitle =  rootView.findViewById(R.id.textViewFragmentQuestionYesNoCaption);
        tvComment = rootView.findViewById(R.id.textViewFragmentQuestionYesNoComment);
        tvStatus = rootView.findViewById(R.id.textViewFragmentQuestionYesNoStatus);

        Button btnYes = rootView.findViewById(R.id.buttonFragmentQuestionYesNoYes);
        Button btnNo = rootView.findViewById(R.id.buttonFragmentQuestionYesNoNo);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        imageViewQuestion.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i(App.TAG, this.getClass().getName() + "onStart called.");

        //Supervisor has bypassed the question.
        if (getArguments().getBoolean("supervisor_bypass", false))
        {
            this.nextQuestion(getViewModel());
            return;
        }

        Question question = getViewModel().getQuestion();
        if (question != null)
        {
            TextView tv = tvTitle;


            if (question.getIsNegativePositive()) {
                Random rand = new Random();
                this.isNegativeQuestion = rand.nextInt() % 2 > 0;
                if (this.isNegativeQuestion) {
                    tv.setText(question.getTitleAlternative());
                    tvComment.setText(question.getAltComment());
                }
                else {
                    tv.setText(question.getTitle());
                    tvComment.setText(question.getComment());
                }

            }
            else {
                tv.setText(question.getTitle());
                tvComment.setText(question.getComment());
            }

            if (Question.hasImage(question)) imageViewQuestion.setImageBitmap(question.getImageQuestion().getThumbNail());

            this.setProgress(tvStatus);



        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected boolean canOperateMachine() {
        return true;
    }


    @Override
    public void onClick(View v) {

        //Record response.
        if (v.getId() == R.id.buttonFragmentQuestionYesNoYes ||  v.getId() == R.id.buttonFragmentQuestionYesNoNo)
        {
            getViewModel().recordResponse(v.getId() == R.id.buttonFragmentQuestionYesNoYes, this.isNegativeQuestion);
            this.nextQuestion(getViewModel());
        }
        else if (v.getId() == R.id.imageViewFragmentQuestionYesNoPic1)
        {
            Question question = getViewModel().getQuestion();
            if (Question.hasImage(question)) {
                try {
                    DialogFullImage dialogFullImage = new DialogFullImage(question.getImageQuestion());
                    dialogFullImage.show(getParentFragmentManager(), "dialog_full_image");
                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print(ex.getMessage());
                }
            }
        }



    }
}
