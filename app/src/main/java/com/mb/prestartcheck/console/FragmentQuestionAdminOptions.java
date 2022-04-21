package com.mb.prestartcheck.console;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.ParameterImageSelect;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Role;
import com.mb.prestartcheck.Roles;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class FragmentQuestionAdminOptions extends Fragment implements View.OnClickListener {
    private View rootView;
    private ViewModelFragmentQuestionAdminOptions viewModel;
    private EditText eTitle;
    private EditText eComment;
    private EditText eSeq;
    private Switch swActive;
    private Switch swIsCritical;
    private Switch swEnableMech;
    private Switch swRandomize;

    private EditText eOpt1;
    private EditText eOpt2;
    private EditText eTimeout;

    private ImageView imageViewOptionOne;
    private ImageView imageViewOptionTwo;
    private Button btnImageOptionOne;
    private Button btnImageOptionTwo;
    private Button btnImageOptionOneClear;
    private Button btnImageOptionTwoClear;

    private Spinner spinnerExpectedResponse;

    //Keep Selection state.
    private String imageOptionOneUri = "";
    private String imageOptionTwoUri = "";

    private Bitmap bmOptionOneThumbnail = null;
    private Bitmap bmOptionTwoThumbnail = null;

    private boolean clearImageOne = false;
    private boolean clearImageTwo = false;

    //Used to store state.
    private Hashtable<String, String> currentStateQuestion = new Hashtable<String, String>();

    protected ActivityResultLauncher<String> contract;
    private int idResImageButton = R.id.buttonFragmentQuestionAdminOpt1Image;
    private ArrayList<String> listExpectedAnswers = new ArrayList<String>();

    private ArrayAdapter<String> spinnerArrayAdapter;
    private TextWatcher textWatcher;

    private ConstraintLayout cLayout;
    QuestionOptions questionModelBeforeChange = null;
    Integer timeout;
    boolean isNew;
    String expectedAnswer = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        questionModelBeforeChange = new QuestionOptions();
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

        this.rootView = inflater.inflate(R.layout.fragment_question_admin_options, container, false);
        long qid = getArguments().getLong("question_id", -1);
        long sid = getArguments().getLong("section_id", -1);
        isNew = getArguments().getBoolean("is_new", false);

        imageViewOptionOne = this.rootView.findViewById(R.id.imageViewFragmentQuestionAdminOption1);
        imageViewOptionTwo = this.rootView.findViewById(R.id.imageViewFragmentQuestionAdminOption2);

        eTitle = this.rootView.findViewById(R.id.editTextFrgamentQuestionAdminOptionsTitle);
        eComment = this.rootView.findViewById(R.id.editTextFrgamentQuestionAdminOptionsComment);
        eSeq = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminOptionsSeq);
        swActive = this.rootView.findViewById(R.id.switchFragmentQuestionAdminOptionsActive);
        swIsCritical = this.rootView.findViewById(R.id.switchFragmentQuestionAdminOptionsIsCrit);
        swEnableMech = this.rootView.findViewById(R.id.switchFragmentQuestionAdminOptionsEnableMech);
        swRandomize = this.rootView.findViewById(R.id.switchFragmentQuestionAdminOptionsRandomize);

        eOpt1 = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminOptionsOne);
        eOpt2 = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminOptionsTwo);

        eTimeout = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminOptionsTimeout);

        btnImageOptionOne = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminOpt1Image);
        btnImageOptionTwo = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminOpt2Image);
        btnImageOptionOneClear = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminOpt1ImageClear);
        btnImageOptionTwoClear = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminOpt2ImageClear);


        spinnerExpectedResponse = this.rootView.findViewById(R.id.spinnerFragmentQuestionAdminOptionsExpectedResponse);
        cLayout = this.rootView.findViewById(R.id.constraintLayoutQuestionAdminOptions);

        if (spinnerArrayAdapter == null) {
            listExpectedAnswers.add(getString(R.string.fragment_question_options_button_no_expected_answer));
            listExpectedAnswers.add(getString(R.string.fragment_question_options_button_option_1));
            listExpectedAnswers.add(getString(R.string.fragment_question_options_button_option_2));
            listExpectedAnswers.add(getString(R.string.fragment_question_options_button_option_none));

            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.content_generic_spinner_item,
                    R.id.textViewcontentGenericSpinnerItem, listExpectedAnswers);


            spinnerExpectedResponse.setAdapter(spinnerArrayAdapter);
        } else
            spinnerExpectedResponse.setAdapter(spinnerArrayAdapter);

        this.viewModel = new ViewModelFragmentQuestionAdminOptions(this.getActivity().getApplication(), qid, sid, isNew);

        btnImageOptionOne.setOnClickListener(this);
        btnImageOptionTwo.setOnClickListener(this);
        btnImageOptionOneClear.setOnClickListener(this);
        btnImageOptionTwoClear.setOnClickListener(this);


        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.viewModel.getQuestion() != null) {
            QuestionOptions question = (QuestionOptions) this.viewModel.getQuestion();
            saveOriginalQuestionModelValues(question);

            QuestionOptions.loadResources(question, getActivity().getApplicationContext());

            if (Session.getInstance().hasPendingImageSelection()) {
                restoreState();
                expectedAnswer = currentStateQuestion.get("expected_answer");
                ParameterImageSelect parameterImageSelect = Session.getInstance().getLastImageSelectionParameters();
                if (parameterImageSelect.getImageIdx() == 1) {

                    imageOptionOneUri = parameterImageSelect.getUri();
                    bmOptionOneThumbnail = parameterImageSelect.getThumbnail();
                    clearImageOne = false;
                } else if (parameterImageSelect.getImageIdx() == 2) {

                    imageOptionTwoUri = parameterImageSelect.getUri();
                    bmOptionTwoThumbnail = parameterImageSelect.getThumbnail();
                    clearImageTwo = false;
                }

            } else {

                eTitle.setText(question.getTitle());
                eComment.setText(question.getComment());
                eSeq.setText(this.viewModel.isNew ? Integer.toString(this.viewModel.getNextQuestionSequence()) : Integer.toString(question.getSequence()));
                swActive.setChecked(question.getEnabled());
                swIsCritical.setChecked(question.getIsCritical());
                swEnableMech.setChecked(question.getAllowMachineOperation());
                //Because there is on negative question for this type of question, store
                //randomness in the negative flag.
                swRandomize.setChecked(question.getIsNegativePositive());

                eOpt1.setText(question.getOption1());
                eOpt2.setText(question.getOption2());

                eTimeout.setText(Integer.toString(question.getTimeout()));

                expectedAnswer = question.getExpectedAnswer();
            }

            if (question.hasOptionOneImage() && imageOptionOneUri.isEmpty())
                this.imageViewOptionOne.setImageBitmap(question.getImageOptionOne().getThumbNail());
            else if (!imageOptionOneUri.isEmpty())
                this.imageViewOptionOne.setImageBitmap(bmOptionOneThumbnail);

            if (question.hasOptionTwoImage() && imageOptionTwoUri.isEmpty())
                this.imageViewOptionTwo.setImageBitmap(question.getImageOptionTwo().getThumbNail());
            else if (!imageOptionTwoUri.isEmpty())
                this.imageViewOptionTwo.setImageBitmap(bmOptionTwoThumbnail);

            if (expectedAnswer != null && !expectedAnswer.isEmpty()) {

                if (expectedAnswer.equals(question.getOption1()))
                    spinnerExpectedResponse.setSelection(1);
                else if (expectedAnswer.equals(question.getOption2()))
                    spinnerExpectedResponse.setSelection(2);
                else if (expectedAnswer.equals(getString(R.string.fragment_question_options_button_option_none)))
                    spinnerExpectedResponse.setSelection(3);
            } else
                spinnerExpectedResponse.setSelection(0);
        }
    }

    private void saveOriginalQuestionModelValues(QuestionOptions question) {
        questionModelBeforeChange.setTitle(question.getTitle());
        questionModelBeforeChange.setComment(question.getComment());
        questionModelBeforeChange.setSequence(question.getSequence());
        questionModelBeforeChange.setIsCritical(question.getIsCritical());
        questionModelBeforeChange.setEnabled(question.getEnabled());
        questionModelBeforeChange.setIsNegativePositive(question.getIsNegativePositive());//RANDOMIZE
        questionModelBeforeChange.setAllowMachineOperation(question.getAllowMachineOperation());
        questionModelBeforeChange.setTimeout(question.getTimeout());
        questionModelBeforeChange.option1 = question.getOption1();
        questionModelBeforeChange.option2 = question.getOption2();
        questionModelBeforeChange.setExpectedAnswer(question.getExpectedAnswer());
        questionModelBeforeChange.setImageOptionOne(question.getImageOptionOne());
        questionModelBeforeChange.setImageOptionTwo(question.getImageOptionTwo());
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
                    "Question Number:",
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
        if (!questionModelBeforeChange.getAllowMachineOperation() == swEnableMech.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question requires machine to be enabled ?",
                    String.valueOf(questionModelBeforeChange.getAllowMachineOperation()),
                    String.valueOf(swEnableMech.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getIsNegativePositive() != swRandomize.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Randomize options",
                    String.valueOf(questionModelBeforeChange.getIsNegativePositive()),
                    String.valueOf(swRandomize.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (questionModelBeforeChange.getTimeout() != timeout) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question Timeout (mins)",
                    String.valueOf(questionModelBeforeChange.getTimeout()),
                    String.valueOf(timeout));
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getExpectedAnswer().equals(expectedAnswer)) {
            AppLog.getInstance().updateValuesChangeString(
                    "Expected answer:",
                    questionModelBeforeChange.getExpectedAnswer(),
                    expectedAnswer);
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getComment().equals(eComment.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Comment",
                    questionModelBeforeChange.getComment(),
                    eComment.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }

        if (!questionModelBeforeChange.getOption1().equals(eOpt1.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Option 1",
                    questionModelBeforeChange.getOption1(),
                    eOpt1.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }
        if (!questionModelBeforeChange.getOption2().equals(eOpt2.getText().toString().trim())) {
            AppLog.getInstance().updateValuesChangeString(
                    "Option 2",
                    questionModelBeforeChange.getOption2(),
                    eOpt2.getText().toString().trim());
            AppLog.getInstance().isValueUpdated = true;
        }

        //Compare the selected image with current image using
        //URI strings.
        String uriBeforeChangeImgOneURI =questionModelBeforeChange.hasOptionOneImage() ?
                questionModelBeforeChange.getImageOptionOne().getUri().toString() : "";

        if (uriBeforeChangeImgOneURI.compareToIgnoreCase(imageOptionOneUri) != 0) {
            AppLog.getInstance().updateValuesChangeString(
                    "Option 1",
                    String.valueOf(uriBeforeChangeImgOneURI),
                    String.valueOf(imageOptionOneUri));
            AppLog.getInstance().isValueUpdated = true;
        }

        String uriBeforeChangeImgTwoURI =questionModelBeforeChange.hasOptionTwoImage() ?
                questionModelBeforeChange.getImageOptionOne().getUri().toString() : "";

        if (uriBeforeChangeImgTwoURI.compareToIgnoreCase(imageOptionTwoUri) != 0) {
            AppLog.getInstance().updateValuesChangeString(
                    "Option 2",
                    String.valueOf(uriBeforeChangeImgTwoURI),
                    String.valueOf(imageOptionTwoUri));
            AppLog.getInstance().isValueUpdated = true;
        }
        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_ADMIN_OPTIONS_TYPE_QUESTION_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean validateInputs() {
        try {

            UIValidator.checkTextEntry(eTitle, this.getView(), R.string.question_title_empty_message);
            UIValidator.checkNumberEntry(eSeq, this.getView(), R.string.question_invalid_number);

            UIValidator.checkTextEntry(eOpt1, this.getView(), R.string.question_option_one_empty_message);
            UIValidator.checkTextEntry(eOpt2, this.getView(), R.string.question_option_two_empty_message);

            //When operating the machine, check the duration of  machine  operation
            //is no longer than  17 mins.
            if (swEnableMech.isChecked() && eTimeout.getText().toString().length() > 0) {
                UIValidator.checkNumberRange(eTimeout, this.getView(),   R.string.question_invalid_timeout_range, 17, 1);
            }
/*
            String strexAns = eExpectedAns.getText().toString().trim();
            if (!strexAns.isEmpty()) {

                String opt1 = eOpt1.getText().toString().trim();
                String opt2 = eOpt2.getText().toString().trim();

                if (strexAns.compareToIgnoreCase(opt1) != 0 &&
                        strexAns.compareToIgnoreCase(opt2) != 0 )
                {

                    AlertManager.showDialog(getContext(), R.string.question_option_invalid_expected_value);
                    return false;
                }
            }


             */


            return true;


        } catch (UIFormatException fex) {
            //TODO : give focus to control
        }

        return false;
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
                FragmentQuestionAdminOptions.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminOptions.this);
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

                ((QuestionOptions) this.viewModel.getQuestion()).removeResources();
                //Check if the image was updated
                if (clearImageOne)
                    this.viewModel.getQuestion().setImageUriOne(null);
                else if (!this.imageOptionOneUri.isEmpty()) {

                    this.viewModel.getQuestion().setImageUriOne(imageOptionOneUri);
                }

                if (clearImageTwo)
                    this.viewModel.getQuestion().setImageUriTwo(null);
                else if (!this.imageOptionTwoUri.isEmpty()) {
                    this.viewModel.getQuestion().setImageUriTwo(imageOptionTwoUri);
                }


                String exAns = spinnerExpectedResponse.getSelectedItem().toString().trim();
                if (exAns.equals(getString(R.string.fragment_question_options_button_option_1)))
                    exAns = eOpt1.getText().toString().trim();
                else if (exAns.equals(getString(R.string.fragment_question_options_button_option_2)))
                    exAns = eOpt2.getText().toString().trim();
                else if (exAns.equals(getString(R.string.fragment_question_options_button_no_expected_answer)))
                    exAns = "";
                else if (exAns.equals(getString(R.string.fragment_question_options_button_option_none)))
                    exAns = "None of the above";


                this.viewModel.updateQuestion(eTitle.getText().toString().trim(),
                        Integer.parseInt(eSeq.getText().toString()),
                        swActive.isChecked(),
                        swIsCritical.isChecked(),
                        swEnableMech.isChecked(),
                        timeout,
                        eOpt1.getText().toString(),
                        eOpt2.getText().toString(),
                        exAns,
                        swRandomize.isChecked(),
                        eComment.getText().toString().trim(),
                        runnable);

                if (!isNew) {
                    reportQuestionChanges();
                } else {
                    AppLog.getInstance().reportEvent(
                            Session.getInstance().getUser().getFullName(),
                            (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_ADMIN_OPTIONS_TYPE_QUESTION_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());
                }
                return true;
            }
            case R.id.action_menu_item_common_delete: {
                Question.delete(FragmentQuestionAdminOptions.this.getActivity().getApplication(),
                        this.viewModel.getQuestion().getId(), new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminOptions.this);
                                        navController.popBackStack();
                                    }
                                });

                            }
                        });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void navigateToPhotoSelection() {
        try {
            boolean isAltImage = idResImageButton == R.id.buttonFragmentQuestionAdminOpt2Image;
            String title = isAltImage ? "Select image for option 2" : "" +
                    "Select image for option 1";

            Bundle bundle = new Bundle();
            bundle.putString("section_title", title);
            bundle.putLong("question_id", this.viewModel.question.getId());
            bundle.putInt("image_index", isAltImage ? 2 : 1);

            saveState();
            Session.getInstance().clearLastParameterImageSelection();


            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_image_select, bundle);

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentQuestionAdminOpt1Image || v.getId() == R.id.buttonFragmentQuestionAdminOpt2Image) {
            idResImageButton = v.getId();
            if (FragmentQuestionAdminHelper.checkPermissions(this, contract)) {
                navigateToPhotoSelection();
            }
        } else if (v.getId() == R.id.buttonFragmentQuestionAdminOpt1ImageClear ||
                v.getId() == R.id.buttonFragmentQuestionAdminOpt2ImageClear) {
            boolean isAltImage = v.getId() == R.id.buttonFragmentQuestionAdminOpt2ImageClear;

            if (isAltImage) {
                clearImageTwo = true;
                imageViewOptionTwo.setImageBitmap(null);
                imageOptionTwoUri = "";
                bmOptionTwoThumbnail = null;
            } else {
                clearImageOne = true;
                imageViewOptionOne.setImageBitmap(null);
                imageOptionOneUri = "";
                bmOptionOneThumbnail = null;

            }


        }
    }

    private void saveState() {
        try {
            currentStateQuestion.clear();
            currentStateQuestion.put("title", eTitle.getText().toString());
            currentStateQuestion.put("question_seq", eSeq.getText().toString());
            currentStateQuestion.put("pos_neg", Boolean.toString(swRandomize.isChecked()));
            currentStateQuestion.put("active", Boolean.toString(swActive.isChecked()));
            currentStateQuestion.put("critical", Boolean.toString(swIsCritical.isChecked()));
            currentStateQuestion.put("machine_enabled", Boolean.toString(swEnableMech.isChecked()));
            currentStateQuestion.put("time_out", eTimeout.getText().toString());

            currentStateQuestion.put("opt1", eOpt1.getText().toString());
            currentStateQuestion.put("opt2", eOpt2.getText().toString());
            currentStateQuestion.put("expected_answer", spinnerExpectedResponse.getSelectedItem().toString().trim());
            currentStateQuestion.put("comment", eComment.getText().toString().trim());

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    private void restoreState() {
        try {
            eTitle.setText(currentStateQuestion.get("title"));
            eSeq.setText(currentStateQuestion.get("question_seq"));
            swRandomize.setChecked(Boolean.parseBoolean(currentStateQuestion.get("pos_neg")));
            swActive.setChecked(Boolean.parseBoolean(currentStateQuestion.get("active")));
            swIsCritical.setChecked(Boolean.parseBoolean(currentStateQuestion.get("critical")));
            swEnableMech.setChecked(Boolean.parseBoolean(currentStateQuestion.get("machine_enabled")));
            eTimeout.setText(currentStateQuestion.get("time_out"));

            eOpt1.setText(currentStateQuestion.get("opt1"));
            eOpt2.setText(currentStateQuestion.get("opt2"));
            eComment.setText(currentStateQuestion.get("comment"));

            //  spinnerExpectedResponse.setSelection(listExpectedAnswers.indexOf(currentStateQuestion.get("expected_answer")));

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
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
