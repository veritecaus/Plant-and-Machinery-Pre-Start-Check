package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Lottery;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;

import java.util.ArrayList;

public class FragmentQuestionMultipleChoice extends FragmentQuestion implements AdaptorMultipleChoice.OnMultipleChoiceItemClickListener,
View.OnClickListener {

    //private ViewModelFragmentQuestionMultipleChoice viewModel;
    private View rootView;
    private ArrayList<String> choices = new ArrayList<String>();
    private AdaptorMultipleChoice adaptor;
    private ImageView imageViewQuestion;
    private TextView tvTitle;
    private TextView tvComment;

    private ViewModelFragmentQuestionMultipleChoice getViewModel() { return (ViewModelFragmentQuestionMultipleChoice)super.viewModel; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Here, we create the view model class so that the onCreateView override
         * can use it.
         */
        long qid = getArguments().getLong("question_id", -1);
        this.viewModel = new ViewModelFragmentQuestionMultipleChoice(getActivity().getApplication(), qid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If specified in the view model, add a menu.
        setHasMenu();

        rootView = inflater.inflate(R.layout.fragment_question_multiple_choice, container, false);
        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.imageViewQuestion = rootView.findViewById(R.id.imageViewFragmentQuestionMultipleChoice);
        tvTitle = rootView.findViewById(R.id.textViewFragmentQuestionMultipleChoiceTitle);
        tvComment = rootView.findViewById(R.id.textViewFragmentQuestionMultipleChoiceComment);

        if (getViewModel().getQuestion() != null) {
            QuestionMultipleChoice question = (QuestionMultipleChoice)getViewModel().getQuestion();

            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewFragmentQuestionMultipleChoice);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adaptor = new AdaptorMultipleChoice(choices, this);
            recyclerView.setAdapter(adaptor);


        }

        this.imageViewQuestion.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Supervisor has bypassed the question.
        if (getArguments().getBoolean("supervisor_bypass", false))
        {
            this.nextQuestion(getViewModel());
            return;
        }

        if (getViewModel().getQuestion() != null)
        {
            QuestionMultipleChoice question = (QuestionMultipleChoice)getViewModel().getQuestion();

            tvTitle.setText(question.getTitle());
            tvComment.setText(question.getComment());
            choices.clear();
            String[] tokens= question.getCustomField1().split(",");

            if (question.getIsNegativePositive())
            {
                ArrayList<Integer> rset = Lottery.getInstance().getRandomSet(tokens.length);
                for(int idx : rset)
                    choices.add(tokens[idx]);

            }
            else {

                for (int idx = 0; idx < tokens.length; idx++)
                    choices.add(tokens[idx]);
            }

            adaptor.notifyDataSetChanged();

            if (Question.hasImage(question)) imageViewQuestion.setImageBitmap(question.getImageQuestion().getThumbNail());

            TextView tvTextStatus = rootView.findViewById(R.id.textViewFragmentQuestionMultipleChoiceStatus);
            this.setProgress(tvTextStatus);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Questioner.getInstance().setHandlerBeforeQuestionChange(null);
    }

    @Override
    protected boolean canOperateMachine() {
        return true;
    }


    @Override
    public void onItemSelected(String s) {

        getViewModel().recordResponse(s);

        this.nextQuestion(getViewModel());


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageViewFragmentQuestionMultipleChoice)
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
        }
    }
}
