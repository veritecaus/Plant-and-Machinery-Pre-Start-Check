package com.mb.prestartcheck.console;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.ParameterImageSelect;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionYesNo;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;


public class FragmentQuestionAdminYesNo extends Fragment implements View.OnClickListener {
    private View rootView;
    private ViewModelFragmentQuestionAdminYesNo viewModel;
    private boolean isNew;
    private Button btnImageBrowse;
    private Button btnClearImage;
    private ImageView imgQuestion;
    private EditText editTitle;
    private EditText editComment;
    private EditText editSeq;
    private EditText editAltTitle;
    private EditText editNegComment;
    private EditText editTimeout;
    private Switch swPosNeg;
    private Switch swActive;
    private Switch swIsCrtical;
    private Switch swMechEnabled;
    private Switch swExpectedAnswer;
    private Switch swAltExpectedAnswer;
    private Switch swRequiresAnswer;
    private TextView tPosNeg;
    //Keep selection state.
    private String imageQuestionUri = "";
    private Bitmap bmQuestionImage = null;
    private boolean clearImageQuestion = false;
    //Used to store state.
    private Hashtable<String, String> currentStateQuestion = new Hashtable<String, String>();
    protected ActivityResultLauncher<String> contract;

    private TextView tExRespCap;
    private TextView tAltExRespCap;
    private TextView tYesLabel1;
    private TextView tYesLabel2;
    private ConstraintLayout clayout;
    QuestionYesNo questionModelBeforeChange = null;
    int sequence;
    int timeOut;
    boolean isCritical;
    boolean isNegativePositive;
    boolean allowMachineOperation;
    String titleAlternative = "";
    String title = "";
    String comment = "";
    String commentAlternative = "";
    String expectedAns = "";
    String altExpectedAns = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        questionModelBeforeChange = new QuestionYesNo();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        contract = FragmentQuestionAdminHelper.registerForPermissionGrants(this, new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        navigateToPhotoSelection();
                    }
                });
            }
        });


        this.rootView = inflater.inflate(R.layout.fragment_question_admin_yes_no, container, false);
        long qid = getArguments().getLong("question_id", -1);
        long sid = getArguments().getLong("section_id", -1);
        this.isNew = getArguments().getBoolean("is_new", false);

        btnImageBrowse = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminYesNoImage);
        btnClearImage = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminYesNoImageClear);
        imgQuestion = this.rootView.findViewById(R.id.imageViewFragmentQuestionAdminYesNo);
        editTitle = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminYesNoTitle);
        editComment = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminYesNoComment);
        editSeq = this.rootView.findViewById(R.id.editTextNumberFragmentQuestionAdminYesNoSequence);
        editAltTitle = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminYesNoNegText);
        editNegComment = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminYesNoNegComment);
        editTimeout = this.rootView.findViewById(R.id.editTextNumberFragmentQuestionYesNoTimeout);
        swPosNeg = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoPosNeg);
        swActive = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoActive);
        swIsCrtical = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoIsCritical);
        swMechEnabled = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoEnableMachine);
        swExpectedAnswer = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoExpectedAnswer);
        swAltExpectedAnswer = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoExpectedNegAnswer);
        tPosNeg = this.rootView.findViewById(R.id.textViewFragmentQuestionAdminYesNoAltText);
        swRequiresAnswer = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoRequiresAnswer);

        tExRespCap = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoCapExResp);
        tAltExRespCap = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoCapExAltResp);
        tYesLabel1 = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoY1);
        tYesLabel2 = this.rootView.findViewById(R.id.switchFragmentQuestionAdminYesNoY2);
        clayout = this.rootView.findViewById(R.id.constraintLayoutQuestionAdminYesNo);

        this.viewModel = new ViewModelFragmentQuestionAdminYesNo(getActivity().getApplication(), qid, sid, this.isNew);

        btnImageBrowse.setOnClickListener(this);
        btnClearImage.setOnClickListener(this);

        swRequiresAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateExpectedAnswerStates(isChecked, swPosNeg.isChecked());
            }
        });

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.viewModel.getQuestion() != null) {
            Question question = this.viewModel.getQuestion();
            saveOriginalQuestionModelValues(question);
            Question.loadResources(question, getActivity().getApplicationContext());

            swPosNeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editAltTitle.setEnabled(isChecked);
                    updateExpectedAnswerStates(swRequiresAnswer.isChecked(), swPosNeg.isChecked());
                }
            });

            if (Session.getInstance().hasPendingImageSelection()) {
                //Restore state here.
                restoreState();

                ParameterImageSelect parameterImageSelect = Session.getInstance().getLastImageSelectionParameters();
                if (parameterImageSelect.getImageIdx() == 1) {

                    imageQuestionUri = parameterImageSelect.getUri();
                    bmQuestionImage = parameterImageSelect.getThumbnail();
                    clearImageQuestion = false;
                }

            } else {
                editTitle.setText(question.getTitle());
                editSeq.setText(this.isNew ? Integer.toString(viewModel.getNextQuestionSequence()) : Integer.toString(question.getSequence()));
                swPosNeg.setChecked(question.getIsNegativePositive());
                editAltTitle.setText(question.getTitleAlternative());
                editTimeout.setText(Integer.toString(question.getTimeout()));

                swActive.setChecked(question.getEnabled());
                swIsCrtical.setChecked(question.getIsCritical());
                swMechEnabled.setChecked(question.getAllowMachineOperation());

                editAltTitle.setEnabled(question.getIsNegativePositive());

                boolean expectedAnswer = Boolean.parseBoolean(question.getExpectedAnswer());
                boolean altExpectedAnswer = Boolean.parseBoolean(question.getExpectedAnswerNeg());

                swExpectedAnswer.setChecked(expectedAnswer);
                swAltExpectedAnswer.setChecked(altExpectedAnswer);

                swRequiresAnswer.setChecked((question.getExpectedAnswer() != null && !question.getExpectedAnswer().isEmpty()) ||
                        (question.getExpectedAnswerNeg() != null && !question.getExpectedAnswerNeg().isEmpty()));
                editComment.setText(question.getComment());
                editNegComment.setText(question.getAltComment());
            }


            if (Question.hasImage(question) && imageQuestionUri.isEmpty()) {
                this.imgQuestion.setImageBitmap(question.getImageQuestion().getThumbNail());
            } else if (!imageQuestionUri.isEmpty()) {
                this.imgQuestion.setImageBitmap(bmQuestionImage);
                Uri uri = Uri.parse(imageQuestionUri);
                ImageLocal imageLocal = new ImageLocal(uri, null);
                viewModel.modifiedQuestion.imageQuestion = imageLocal;
            }
            updateExpectedAnswerStates(swRequiresAnswer.isChecked(), swPosNeg.isChecked());
        }
    }

    private void saveOriginalQuestionModelValues(Question question) {
        questionModelBeforeChange.setTitle(viewModel.getQuestion().getTitle());
        title = questionModelBeforeChange.getTitle();
        sequence = question.getSequence();
        timeOut = question.getTimeout();
        isCritical = question.getIsCritical();
        isNegativePositive = question.getIsNegativePositive();
        allowMachineOperation = question.getAllowMachineOperation();
        titleAlternative = question.getTitleAlternative();
        commentAlternative = question.getAltComment();
        comment = question.getComment();
        expectedAns = question.getExpectedAnswer();
        altExpectedAns = question.getExpectedAnswerNeg();

        questionModelBeforeChange.setTitle(title);
        questionModelBeforeChange.setSequence(sequence);
        questionModelBeforeChange.setIsNegativePositive(isNegativePositive);
        questionModelBeforeChange.setTitleAlternative(titleAlternative);
        questionModelBeforeChange.setIsCritical(isCritical);
        questionModelBeforeChange.setAllowMachineOperation(allowMachineOperation);
        questionModelBeforeChange.setTimeout(timeOut);
        questionModelBeforeChange.setExpectedAnswer(expectedAns);
        questionModelBeforeChange.setExpectedAnswerNeg(altExpectedAns);
        questionModelBeforeChange.setComment(comment);
        questionModelBeforeChange.setAltComment(commentAlternative);
        questionModelBeforeChange.setImageQuestion(question.imageQuestion);
        if (null == question.getImageUriOne()) {
            questionModelBeforeChange.setImageUriOne("no image");
        } else {
            questionModelBeforeChange.setImageUriOne(question.getImageUriOne());

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Questioner.getInstance().setHandlerBeforeQuestionChange(null);
    }

    private boolean validateInputs() {
        try {

            UIValidator.checkTextEntry(editTitle, this.getView(), R.string.question_title_empty_message);
            UIValidator.checkTextEntry(editSeq, this.getView(), R.string.question_invalid_number);

            UIValidator.checkNumberEntry(editSeq, this.getView(), R.string.question_invalid_number);
            if (swPosNeg.isChecked())
                UIValidator.checkTextEntry(editAltTitle, this.getView(), R.string.question_alt_title_empty_message);

            //When operating the machine, check the duration of  machine  operation
            //is no longer than  17 mins.
            if (swMechEnabled.isChecked() && editTimeout.getText().toString().length() > 0) {
                UIValidator.checkNumberRange(editTimeout, this.getView(),   R.string.question_invalid_timeout_range, 17, 1);
            }


            return true;


        } catch (UIFormatException fex) {
            //TODO : give focus to control
            fex.getView().requestFocus();
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FragmentQuestionAdminYesNo.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NavController navController
                                = NavHostFragment.findNavController(FragmentQuestionAdminYesNo.this);
                        navController.popBackStack();
                    }
                });
            }
        };

        if (item.getItemId() == R.id.action_menu_item_common_done) {
            //Hide keyboard.
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            View fview = clayout.findFocus();
            if (fview != null)
                imm.hideSoftInputFromWindow(clayout.findFocus().getWindowToken(), 0);

            if (!validateInputs()) return true;

            Integer timeout = UIValidator.safeGetInteger(editTimeout);

            if (timeout == null) {
                editTimeout.setText("");
                timeout = 0;
            }

            this.viewModel.getQuestion().removeResources();

            //Check if the image was updated
            if (clearImageQuestion)
                this.viewModel.getQuestion().setImageUriOne("");
            else if (!this.imageQuestionUri.isEmpty()) {

                this.viewModel.getQuestion().setImageUriOne(imageQuestionUri);
            }

            String expectedAns = swRequiresAnswer.isChecked() ? Boolean.toString(swExpectedAnswer.isChecked()) : "";
            String altExpectedAns = swPosNeg.isChecked() && swRequiresAnswer.isChecked() ? Boolean.toString(swAltExpectedAnswer.isChecked())
                    : "";

            this.viewModel.updateQuestion(editTitle.getText().toString().trim(),
                    Integer.parseInt(editSeq.getText().toString()),
                    swPosNeg.isChecked(),
                    editAltTitle.getText().toString().trim(),
                    swActive.isChecked(),
                    swIsCrtical.isChecked(),
                    swMechEnabled.isChecked(),
                    timeout,
                    expectedAns,
                    altExpectedAns,
                    editComment.getText().toString().trim(),
                    editNegComment.getText().toString().trim(),
                    runnable);

            if (!isNew) {
                reportQuestionChanges();
            } else {
                AppLog.getInstance().reportEvent(
                        Session.getInstance().getUser().getFullName(),
                        (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                        ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_YES_NO_TYPE_QUESTION_ACTION.name(),
                        ResultEnum.RESULT_SUCCESS.name());
            }
            return true;

        } else if (item.getItemId() == R.id.action_menu_item_common_delete) {
            Question.delete(this.getActivity().getApplication(), this.viewModel.getQuestion().getId(),
                    new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminYesNo.this);
                                    navController.popBackStack();
                                }
                            });
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reportQuestionChanges() {
        // check to identify which fields were changed on this

        if (!questionModelBeforeChange.getTitle().equals(viewModel.modifiedQuestion.getTitle())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Title",
                    questionModelBeforeChange.getTitle(),
                    viewModel.modifiedQuestion.getTitle());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getTitleAlternative().equals(viewModel.modifiedQuestion.getTitleAlternative())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Title Alternative",
                    questionModelBeforeChange.getTitleAlternative(),
                    viewModel.modifiedQuestion.getTitleAlternative());
            AppLog.getInstance().isValueUpdated = true;
        }

        if (!questionModelBeforeChange.getComment().equals(viewModel.modifiedQuestion.getComment())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Comment",
                    questionModelBeforeChange.getComment(),
                    viewModel.modifiedQuestion.getComment());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getAltComment().equals(viewModel.modifiedQuestion.getAltComment())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Alternative Comment",
                    questionModelBeforeChange.getAltComment(),
                    viewModel.modifiedQuestion.getAltComment());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getExpectedAnswer().equals(viewModel.modifiedQuestion.getExpectedAnswer())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Expected Answere",
                    questionModelBeforeChange.getExpectedAnswer(),
                    viewModel.modifiedQuestion.getExpectedAnswer());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getExpectedAnswerNeg().equals(viewModel.modifiedQuestion.getExpectedAnswerNeg())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Expected Answere Negative",
                    questionModelBeforeChange.getExpectedAnswerNeg(),
                    viewModel.modifiedQuestion.getExpectedAnswerNeg());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getTimeout() != viewModel.modifiedQuestion.getTimeout()) {
            AppLog.getInstance().updateValuesChangeString(
                    "TimeOut",
                    String.valueOf(questionModelBeforeChange.getTimeout()),
                    String.valueOf(viewModel.modifiedQuestion.getTimeout()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getSequence() != viewModel.modifiedQuestion.getSequence()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Sequence",
                    String.valueOf(questionModelBeforeChange.getSequence()),
                    String.valueOf(viewModel.modifiedQuestion.getSequence()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getIsNegativePositive() == viewModel.modifiedQuestion.getIsNegativePositive()) {
            AppLog.getInstance().updateValuesChangeString(
                    "IsNegativePositive",
                    String.valueOf(questionModelBeforeChange.getIsNegativePositive()),
                    String.valueOf(viewModel.modifiedQuestion.getIsNegativePositive()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getAllowMachineOperation() == viewModel.modifiedQuestion.getAllowMachineOperation()) {
            AppLog.getInstance().updateValuesChangeString(
                    "AllowMachineOperation",
                    String.valueOf(questionModelBeforeChange.getAllowMachineOperation()),
                    String.valueOf(viewModel.modifiedQuestion.getAllowMachineOperation()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getIsCritical() == viewModel.modifiedQuestion.getIsCritical()) {
            AppLog.getInstance().updateValuesChangeString(
                    "IsCritical",
                    String.valueOf(questionModelBeforeChange.getIsCritical()),
                    String.valueOf(viewModel.modifiedQuestion.getIsCritical()));
            AppLog.getInstance().isValueUpdated = true;
        }

        if (!questionModelBeforeChange.getImageUriOne().equals(imageQuestionUri)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Image",
                    String.valueOf(questionModelBeforeChange.getImageUriOne()),
                    String.valueOf(imageQuestionUri));
            AppLog.getInstance().isValueUpdated = true;
        }
        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_YES_NO_TYPE_QUESTION_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_common_actions, menu);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentQuestionAdminYesNoImage) {
            if (FragmentQuestionAdminHelper.checkPermissions(this, contract)) {
                navigateToPhotoSelection();
            }

        } else if (v.getId() == R.id.buttonFragmentQuestionAdminYesNoImageClear) {
            clearImageQuestion = true;
            imgQuestion.setImageBitmap(null);
            imageQuestionUri = "";
            bmQuestionImage = null;
        }
    }


    private void saveState() {
        try {
            currentStateQuestion.clear();
            currentStateQuestion.put("title", editTitle.getText().toString());
            currentStateQuestion.put("question_seq", editSeq.getText().toString());
            currentStateQuestion.put("pos_neg", Boolean.toString(swPosNeg.isChecked()));
            currentStateQuestion.put("alt_title", editAltTitle.getText().toString());
            currentStateQuestion.put("active", Boolean.toString(swActive.isChecked()));
            currentStateQuestion.put("critical", Boolean.toString(swIsCrtical.isChecked()));
            currentStateQuestion.put("machine_enabled", Boolean.toString(swMechEnabled.isChecked()));
            currentStateQuestion.put("time_out", editTimeout.getText().toString());
            currentStateQuestion.put("expected_answer", Boolean.toString(swExpectedAnswer.isChecked()));
            currentStateQuestion.put("alt_expected_answer", Boolean.toString(swAltExpectedAnswer.isChecked()));
            currentStateQuestion.put("requires_answer", Boolean.toString(swRequiresAnswer.isChecked()));
            currentStateQuestion.put("comment", editComment.getText().toString());
            currentStateQuestion.put("alt_comment", editNegComment.getText().toString());
            //here compare data model changes and log event on navigation
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    private void restoreState() {
        try {
            editTitle.setText(currentStateQuestion.get("title"));
            editSeq.setText(currentStateQuestion.get("question_seq"));
            swPosNeg.setChecked(Boolean.parseBoolean(currentStateQuestion.get("pos_neg")));
            editAltTitle.setText(currentStateQuestion.get("alt_title"));
            swActive.setChecked(Boolean.parseBoolean(currentStateQuestion.get("active")));
            swIsCrtical.setChecked(Boolean.parseBoolean(currentStateQuestion.get("critical")));
            swMechEnabled.setChecked(Boolean.parseBoolean(currentStateQuestion.get("machine_enabled")));
            editTimeout.setText(currentStateQuestion.get("time_out"));
            swExpectedAnswer.setChecked(Boolean.parseBoolean(currentStateQuestion.get("expected_answer")));
            swAltExpectedAnswer.setChecked(Boolean.parseBoolean(currentStateQuestion.get("alt_expected_answer")));
            swRequiresAnswer.setChecked(Boolean.parseBoolean(currentStateQuestion.get("requires_answer")));
            editComment.setText(currentStateQuestion.get("comment"));
            editNegComment.setText(currentStateQuestion.get("alt_comment"));

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    private void navigateToPhotoSelection() {
        Bundle bundle = new Bundle();
        bundle.putString("section_title", "Select image for question");
        bundle.putLong("question_id", this.viewModel.question.getId());
        bundle.putInt("image_index", 1);

        //Save state
        saveState();
        Session.getInstance().clearLastParameterImageSelection();

        final NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.nav_image_select, bundle);
    }

    public void askForPermission() {
        try {
            if (contract != null) contract.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    private void updateExpectedAnswerStates(boolean requiresAnswer, boolean hasNegativeContext) {
        swExpectedAnswer.setEnabled(requiresAnswer);
        swAltExpectedAnswer.setEnabled(requiresAnswer && hasNegativeContext);

        tExRespCap.setEnabled(requiresAnswer);
        tAltExRespCap.setEnabled(requiresAnswer && hasNegativeContext);

        tYesLabel1.setEnabled(requiresAnswer);
        tYesLabel2.setEnabled(requiresAnswer && hasNegativeContext);

    }

}
