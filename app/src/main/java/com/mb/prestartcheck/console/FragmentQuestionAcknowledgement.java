package com.mb.prestartcheck.console;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;

public class FragmentQuestionAcknowledgement extends FragmentQuestion implements  View.OnClickListener {

    private View rootView;
    //private ViewModelFragmentQuestionAck viewModel;
    private ImageView imageViewQuestion;
    private TextView tvTitle;
    private TextView tvComment;
    public  ViewModelFragmentQuestionAck getViewModel() { return (ViewModelFragmentQuestionAck)super.viewModel;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Here, we create the view model class so that the onCreateView override
         * can use it.
         */
        long qid = getArguments().getLong("question_id", -1);
        super.viewModel = new ViewModelFragmentQuestionAck(getActivity().getApplication(), qid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If specified in the view model, add a menu.
        setHasMenu();
        //Inflate fragment and return it.
        rootView = inflater.inflate(R.layout.fragment_question_acknowledgement, container, false);

        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageViewQuestion = this.rootView.findViewById(R.id.imageViewFragmentQuestionAck);
        if (getViewModel().getQuestion() != null)
        {
            Button btnProceed = rootView.findViewById(R.id.buttonFragmentQuestionAckProceed);
            btnProceed.setOnClickListener(this);
        }

        imageViewQuestion.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        Question question = getViewModel().getQuestion();
        if (question != null)
        {

            TextView tvTitle = rootView.findViewById(R.id.textViewFragmentQuestionAckTitle);
            tvTitle.setText(getViewModel().getQuestion().getTitle());

            TextView tvComment = rootView.findViewById(R.id.textViewFragmentQuestionAckComment);
            tvComment.setText(getViewModel().getQuestion().getComment());

            if (Question.hasImage(question)) imageViewQuestion.setImageBitmap(question.getImageQuestion().getThumbNail());

            TextView tvStatus = rootView.findViewById(R.id.textViewFragmentQuestionAckStatus);
            this.setProgress(tvStatus);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Questioner.getInstance().setHandlerBeforeQuestionChange(null);
    }

    @Override
    protected boolean canOperateMachine() {
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageViewFragmentQuestionAck)
        {
            Question question = getViewModel().getQuestion();
            if (question != null && Question.hasImage(question)) {
                try {
                    DialogFullImage dialogFullImage = new DialogFullImage(question.getImageQuestion());
                    dialogFullImage.show(getParentFragmentManager(), "dialog_full_image");
                }
                catch(Exception ex)
                {
                    AppLog.getInstance().print(ex.getMessage());
                }
            }

            return;
        }

        getViewModel().recordResponse();
        // Record response here.
        this.nextQuestion(getViewModel());
    }
}
