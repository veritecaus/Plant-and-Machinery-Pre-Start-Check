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
import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.Lottery;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.R;

public class FragmentQuestionOptions extends FragmentQuestion implements View.OnClickListener {

    //private ViewModelQuestionOptions viewModel;
    private View rootView;
    public final static String DIALOG_TAG_CUSTOM_RESPONSE = "custom_response";
    DialogResponseCustom dialogResponseCustom = new DialogResponseCustom(this);
    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private ImageView imageViewOptionOne;
    private ImageView imageViewOptionTwo;
    private boolean isOptionSwapped = false;
    private TextView tvTitle;
    private TextView tvComment;

    private ViewModelFragmentQuestionOptions getViewModel() { return (ViewModelFragmentQuestionOptions)super.viewModel;}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Here, we create the view model class so that the onCreateView override
         * can use it.
         */
        long qid = getArguments().getLong("question_id", -1);
        super.viewModel = new ViewModelFragmentQuestionOptions(getActivity().getApplication(), qid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        //If specified in the view model, add a menu.
        setHasMenu();

        rootView = inflater.inflate(R.layout.fragment_question_options, container, false);
        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvTitle = rootView.findViewById(R.id.textViewFragmentQuestionOptionsTitle);
        tvComment = rootView.findViewById(R.id.textViewFragmentQuestionOptionsComment);

        imageViewOptionOne = this.rootView.findViewById(R.id.imageViewFragmentQuestionOptionsImage1);
        imageViewOptionTwo = this.rootView.findViewById(R.id.imageViewFragmentQuestionOptionsImage2);

        //next question.
        btnOption1 = rootView.findViewById(R.id.buttonFragmentOptionsOne);
        btnOption2 = rootView.findViewById(R.id.buttonFragmentOptionsTwo);
        btnOption3 = rootView.findViewById(R.id.buttonFragmentOptionsNone);

        //When the operator presses a button, record the response and proceed to the
        btnOption1.setOnClickListener(this);
        btnOption2.setOnClickListener(this);
        btnOption3.setOnClickListener(this);


        imageViewOptionOne.setOnClickListener(this);
        imageViewOptionTwo.setOnClickListener(this);


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
            QuestionOptions question = (QuestionOptions)getViewModel().getQuestion();

            tvTitle.setText(question.getTitle());
            tvComment.setText(question.getComment());

            TextView tvStatus = rootView.findViewById(R.id.textViewFragmentQuestionOptionsStatus);

            this.setProgress(tvStatus);

            //TODO: Assign images.

            //When the operator presses a button, record the response and proceed to the
            //next question.


            int optOneIdx  =1 ;
            if (question.getIsNegativePositive())
            {
                optOneIdx =   (Lottery.getInstance().getNumber(1000) % 2) == 0 ? 2 : 1;
            }

            if (optOneIdx == 2)
            {
                //Reverse option 1 & 2.
                btnOption1.setText(question.getOption2());
                btnOption2.setText(question.getOption1());


                if (question.hasOptionTwoImage())
                {
                    imageViewOptionOne.setImageBitmap(question.getImageOptionTwo().getDisplayImage());
                    imageViewOptionOne.setTag(question.getImageOptionTwo());
                }
                if (question.hasOptionOneImage()) {
                    imageViewOptionTwo.setImageBitmap(question.getImageOptionOne().getDisplayImage());
                    imageViewOptionTwo.setTag(question.getImageOptionOne());
                }

                isOptionSwapped = true;

            }
            else
            {
                btnOption1.setText(question.getOption1());
                btnOption2.setText(question.getOption2());


                if (question.hasOptionOneImage())
                {
                    imageViewOptionOne.setImageBitmap(question.getImageOptionOne().getDisplayImage());
                    imageViewOptionOne.setTag(question.getImageOptionOne());
                }
                if (question.hasOptionTwoImage())
                {
                    imageViewOptionTwo.setImageBitmap(question.getImageOptionTwo().getDisplayImage());
                    imageViewOptionTwo.setTag(question.getImageOptionTwo());
                }

                isOptionSwapped = false;

            }


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

        if (getViewModel().getQuestion() == null) {
            return;
        }


        if ((v.getId() == R.id.imageViewFragmentQuestionOptionsImage1 ||  v.getId() == R.id.imageViewFragmentQuestionOptionsImage2)
        && v.getTag() != null)
        {
            try {

                DialogFullImage dialogFullImage = new DialogFullImage((ImageLocal)v.getTag());
                dialogFullImage.show(getParentFragmentManager(), "dialog_full_image");
            }
            catch(Exception ex)
            {
                AppLog.getInstance().print(ex.getMessage());
            }
            return;
        }

        QuestionOptions questionOptions = (QuestionOptions)getViewModel().getQuestion();

        // Record response here.
         if (v.getId() == R.id.buttonFragmentOptionsOne)
         {
            getViewModel().recordResponse(isOptionSwapped ? questionOptions.getOption2()  :
                    questionOptions.getOption1());
         }
         else if (v.getId() == R.id.buttonFragmentOptionsTwo)
         {
             getViewModel().recordResponse(isOptionSwapped ? questionOptions.getOption1():
                     questionOptions.getOption2());
         }
         else if (v.getId() == R.id.buttonFragmentOptionsNone)
         {
             getViewModel().recordResponse(getString(R.string.fragment_question_options_button_option_none) );
             //dialogResponseCustom.show(getChildFragmentManager(), DIALOG_TAG_CUSTOM_RESPONSE);
             //return;
         }

         this.nextQuestion(getViewModel());

    }

    public void setOperatorResponse()
    {
        getViewModel().recordResponse(dialogResponseCustom.getResponse());
        nextQuestion(getViewModel());
    }

    private void setOptionTitle(final TextView textView, String val, boolean hideEmpty)
    {
        String tmp = val == null && (val != null && val.isEmpty()) ? "" : val;

        textView.setText(tmp);
        if (hideEmpty)
            textView.setVisibility(tmp.equals("") ? View.INVISIBLE  : View.VISIBLE);
    }

}
