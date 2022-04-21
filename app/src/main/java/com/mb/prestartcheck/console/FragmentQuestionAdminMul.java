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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

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
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class FragmentQuestionAdminMul extends Fragment implements View.OnClickListener {
    private View rootView;
    private ViewModelFragmentQuestionAdminMul viewModel;
    private EditText eTitle;
    private EditText eComment;
    private EditText eSeq;
    private Switch swActive;
    private Switch swIsCritical;
    private Switch swMechOp;
    private Switch swIsRandomize;
    private EditText eCustomField1;
    private EditText eTimeout;
    private EditText eExpectedAns;
    private Button btnImageBrowse;
    private Button btnClearImage;
    private ImageView imgQuestion;
    //Keep selection state.
    private String imageQuestionUri = "";
    private Bitmap bmQuestionImage = null;
    private boolean clearImageQuestion = false;

    private ConstraintLayout cLayout;
    //Used to store state.
    private Hashtable<String, String> currentStateQuestion = new Hashtable<String, String>();

    protected ActivityResultLauncher<String> contract;
    QuestionMultipleChoice questionModelBeforeChange = null;
    Integer timeout;
    boolean isNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        questionModelBeforeChange = new QuestionMultipleChoice();
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
        this.rootView = inflater.inflate(R.layout.fragment_question_admin_mul, container, false);
        long qid = getArguments().getLong("question_id", -1);
        long sid = getArguments().getLong("section_id", -1);
        isNew = getArguments().getBoolean("is_new", false);

        btnImageBrowse = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminMulBrowse);
        btnClearImage = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminMulClear);
        imgQuestion = this.rootView.findViewById(R.id.imageViewFragmentQuestionAdminMul);

        eTitle = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulTitle);
        eComment = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulComment);
        eSeq = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulSeq);
        swActive = this.rootView.findViewById(R.id.switchFragmentQuestionAdminMulActive);
        swIsCritical = this.rootView.findViewById(R.id.switchQuestionAdminMulIsCritical);
        swMechOp = this.rootView.findViewById(R.id.switchFragmentQuestionAdminMulMechOp);
        swIsRandomize = this.rootView.findViewById(R.id.switchFragmentQuestionAdminMulRandomize);

        eCustomField1 = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulCustomField);
        eTimeout = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulTimeout);
        eExpectedAns = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminMulExpectedAns);
        cLayout = this.rootView.findViewById(R.id.contraintLayoutQuestionAdminMul);

        btnImageBrowse.setOnClickListener(this);
        btnClearImage.setOnClickListener(this);

        this.viewModel = new ViewModelFragmentQuestionAdminMul(this.getActivity().getApplication(), qid, sid, isNew);
        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.viewModel.getQuestion() != null) {
            QuestionMultipleChoice question = (QuestionMultipleChoice) this.viewModel.getQuestion();
            saveOriginalQuestionModelValues(question);
            Question.loadResources(question, getActivity().getApplicationContext());

            if (Session.getInstance().hasPendingImageSelection()) {
                restoreState();
                ParameterImageSelect parameterImageSelect = Session.getInstance().getLastImageSelectionParameters();
                if (parameterImageSelect.getImageIdx() == 1) {

                    imageQuestionUri = parameterImageSelect.getUri();
                    bmQuestionImage = parameterImageSelect.getThumbnail();
                    clearImageQuestion = false;
                }

            } else {
                eTitle.setText(question.getTitle());
                eSeq.setText(this.viewModel.isNew ? Integer.toString(this.viewModel.getNextQuestionSequence()) : Integer.toString(question.getSequence()));
                swActive.setChecked(question.getEnabled());
                swIsCritical.setChecked(question.getIsCritical());
                swMechOp.setChecked(question.getAllowMachineOperation());
                swIsRandomize.setChecked(question.getIsNegativePositive());
                eCustomField1.setText(question.getCustomField1());
                eTimeout.setText(Integer.toString(question.getTimeout()));
                eExpectedAns.setText(question.getExpectedAnswer());
                eComment.setText(question.getComment());
            }

            if (Question.hasImage(question) && imageQuestionUri.isEmpty())
                this.imgQuestion.setImageBitmap(question.getImageQuestion().getThumbNail());
            else if (!imageQuestionUri.isEmpty())
                this.imgQuestion.setImageBitmap(bmQuestionImage);
            Uri uri = Uri.parse(imageQuestionUri);
            ImageLocal imageLocal = new ImageLocal(uri, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_common_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FragmentQuestionAdminMul.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminMul.this);
                        navController.popBackStack();
                    }
                });
            }
        };
        switch (item.getItemId()) {
            case R.id.action_menu_item_common_done: {
                //Hide keyboard.
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                View fview = cLayout.findFocus();
                if (fview != null)
                    imm.hideSoftInputFromWindow(cLayout.findFocus().getWindowToken(), 0);

                if (!validateInputs()) return true;

                timeout = UIValidator.safeGetInteger(eTimeout);

                if (timeout == null) {
                    eTimeout.setText("");
                    timeout = 0;
                }

                this.viewModel.getQuestion().removeResources();

                //Check if the image was updated
                if (clearImageQuestion)
                    this.viewModel.getQuestion().setImageUriOne(null);
                else if (!this.imageQuestionUri.isEmpty()) {

                    this.viewModel.getQuestion().setImageUriOne(imageQuestionUri);
                }

                this.viewModel.updateQuestion(eTitle.getText().toString().trim(),
                        Integer.parseInt(eSeq.getText().toString()),
                        swActive.isChecked(),
                        swIsCritical.isChecked(),
                        swMechOp.isChecked(),
                        timeout,
                        eCustomField1.getText().toString().trim(),
                        eExpectedAns.getText().toString().trim(),
                        swIsRandomize.isChecked(),
                        eComment.getText().toString().trim(),
                        runnable);

                if (!isNew) {
                    reportQuestionChanges();
                } else {
                    AppLog.getInstance().reportEvent(
                            Session.getInstance().getUser().getFullName(),
                            (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_MULTIPLE_CHOICE_TYPE_QUESTION_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());
                }

                return true;
            }
            case R.id.action_menu_item_common_delete: {
                Question.delete(this.getActivity().getApplication(), this.viewModel.getQuestion().getId(),
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminMul.this);
                                        navController.popBackStack();
                                    }
                                });
                            }
                        });
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentQuestionAdminMulBrowse) {
            if (FragmentQuestionAdminHelper.checkPermissions(this, contract)) {
                navigateToPhotoSelection();
            }

        } else if (v.getId() == R.id.buttonFragmentQuestionAdminMulClear) {
            clearImageQuestion = true;
            imgQuestion.setImageBitmap(null);
            imageQuestionUri = "";
            bmQuestionImage = null;
        }
    }

    private boolean validateInputs() {
        try {

            UIValidator.checkTextEntry(eTitle, this.getView(), R.string.question_title_empty_message);
            UIValidator.checkTextEntry(eSeq, this.getView(), R.string.question_invalid_number);

            if (!eSeq.getText().toString().isEmpty())
                UIValidator.checkNumberEntry(eSeq, this.getView(), R.string.question_invalid_number);

            //When operating the machine, check the duration of  machine  operation
            //is no longer than  17 mins.
            if (swMechOp.isChecked() && eTimeout.getText().toString().length() > 0) {
                UIValidator.checkNumberRange(eTimeout, this.getView(),   R.string.question_invalid_timeout_range, 17, 1);
            }

            UIValidator.checkTextEntry(eCustomField1, this.getView(), R.string.question_multi_csv_empty_message);

            if (eExpectedAns.getText().toString().length() > 0) {
                //Check that the expected answer is in the list of
                //of selectable choices.
                String[] tokens = eCustomField1.getText().toString().split(",");
                String expected = eExpectedAns.getText().toString().trim();
                boolean contains = false;
                if (tokens != null && tokens.length > 0) {
                    for (String s : tokens) {
                        if (s.compareToIgnoreCase(expected) == 0) {
                            contains = true;
                            break;
                        }
                    }

                    if (!contains) {
                        AlertManager.showDialog(getContext(), R.string.question_multi_invalid_expected_value);
                        return false;
                    }
                }

                //Ensure that the number of possible choices is less than equal to four.

                if (tokens.length > 4)
                {
                    AlertManager.showDialog(getContext(), R.string.question_multi_invalid_choice_length);
                    return false;
                }

            }

            return true;


        } catch (UIFormatException fex) {
            //TODO : give focus to control
            fex.getView().requestFocus();
        }

        return false;
    }

    private void saveState() {
        try {
            currentStateQuestion.clear();
            currentStateQuestion.put("title", eTitle.getText().toString());
            currentStateQuestion.put("question_seq", eSeq.getText().toString());
            currentStateQuestion.put("pos_neg", Boolean.toString(swIsRandomize.isChecked()));
            currentStateQuestion.put("active", Boolean.toString(swActive.isChecked()));
            currentStateQuestion.put("critical", Boolean.toString(swIsCritical.isChecked()));
            currentStateQuestion.put("machine_enabled", Boolean.toString(swMechOp.isChecked()));
            currentStateQuestion.put("time_out", eTimeout.getText().toString());
            currentStateQuestion.put("customField1", eCustomField1.getText().toString());
            currentStateQuestion.put("expected_answer", eExpectedAns.getText().toString());
            currentStateQuestion.put("comment", eComment.getText().toString());
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    private void restoreState() {
        try {
            eTitle.setText(currentStateQuestion.get("title"));
            eSeq.setText(currentStateQuestion.get("question_seq"));
            swIsRandomize.setChecked(Boolean.parseBoolean(currentStateQuestion.get("pos_neg")));
            swActive.setChecked(Boolean.parseBoolean(currentStateQuestion.get("active")));
            swIsCritical.setChecked(Boolean.parseBoolean(currentStateQuestion.get("critical")));
            swMechOp.setChecked(Boolean.parseBoolean(currentStateQuestion.get("machine_enabled")));
            eTimeout.setText(currentStateQuestion.get("time_out"));
            eCustomField1.setText(currentStateQuestion.get("customField1"));
            eExpectedAns.setText(currentStateQuestion.get("expected_answer"));
            eComment.setText(currentStateQuestion.get("comment"));

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    private void navigateToPhotoSelection() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("section_title", "Select image for question");
            bundle.putLong("question_id", this.viewModel.question.getId());
            bundle.putInt("image_index", 1);

            saveState();
            Session.getInstance().clearLastParameterImageSelection();

            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_image_select, bundle);

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

     private void saveOriginalQuestionModelValues(QuestionMultipleChoice question) {
        questionModelBeforeChange.setTitle(question.getTitle());
        questionModelBeforeChange.setComment(question.getComment());
        questionModelBeforeChange.setSequence(question.getSequence());
        questionModelBeforeChange.setIsCritical(question.getIsCritical());
        questionModelBeforeChange.setEnabled(question.getEnabled());
        questionModelBeforeChange.setIsNegativePositive(question.getIsNegativePositive());
        questionModelBeforeChange.setAllowMachineOperation(question.getAllowMachineOperation());
        questionModelBeforeChange.setTimeout(question.getTimeout());
        questionModelBeforeChange.setCustomField1(question.getCustomField1());
        questionModelBeforeChange.setExpectedAnswer(question.getExpectedAnswer());
        if (null == question.getImageUriOne()) {
            questionModelBeforeChange.setImageUriOne("no image");
        } else {
            questionModelBeforeChange.setImageUriOne(question.getImageUriOne());
        }
    }

    private void reportQuestionChanges() {
        // check to identify which fields were changed on this
        if (!questionModelBeforeChange.getTitle().equals(eTitle.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Title",
                    questionModelBeforeChange.getTitle(),
                    eTitle.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getSequence() != Integer.parseInt(eSeq.getText().toString())) {
            AppLog.getInstance().updateValuesChangeString(
                    "number",
                    String.valueOf(questionModelBeforeChange.getSequence()),
                    String.valueOf(eSeq.getText().toString()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getEnabled() == swActive.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question active",
                    String.valueOf(questionModelBeforeChange.getEnabled()),
                    String.valueOf(swActive.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getIsCritical() == swIsCritical.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Incorrect answer stops question process ?",
                    String.valueOf(questionModelBeforeChange.getIsCritical()),
                    String.valueOf(swIsCritical.isChecked())
            );
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getAllowMachineOperation() == swMechOp.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question requires machine to be enabled ?",
                    String.valueOf(questionModelBeforeChange.getAllowMachineOperation()),
                    String.valueOf(swMechOp.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getIsNegativePositive() != swIsRandomize.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Randomize options",
                    String.valueOf(questionModelBeforeChange.getIsNegativePositive()),
                    String.valueOf(swIsRandomize.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getTimeout() != timeout) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question Timeout (mins)",
                    String.valueOf(questionModelBeforeChange.getTimeout()),
                    String.valueOf(timeout));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getCustomField1().equals(eCustomField1.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Values (CSV)",
                    questionModelBeforeChange.getCustomField1(),
                    eCustomField1.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getExpectedAnswer().equals(eExpectedAns.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Expected answer:",
                    questionModelBeforeChange.getExpectedAnswer(),
                    eExpectedAns.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getComment().equals(eComment.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Comment",
                    questionModelBeforeChange.getComment(),
                    eComment.getText().toString().trim());
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
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_MULTIPLE_CHOICE_TYPE_QUESTION_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
        }
    }

    public void askForPermission() {
        try {
            if (contract != null) contract.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }


}

