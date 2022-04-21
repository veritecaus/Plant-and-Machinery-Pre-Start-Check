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
import com.mb.prestartcheck.QuestionAcknowledgement;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.QuestionYesNo;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class FragmentQuestionAdminAck extends Fragment implements View.OnClickListener{

    private View rootView;
    private ViewModelFragmentQuestionAdminAck viewModel;
    private Button btnImageBrowse;
    private Button btnClearImage;
    private ImageView imgQuestion;
    private EditText eTitle;
    private EditText eComment;
    private EditText eSeq;
    private Switch swActive;
    private Switch swMachEnbled;
    private TextView txtTimeoutLabel;
    private EditText eTimeout;

    //Keep selection state.
    private String imageQuestionUri = "";
    private Bitmap bmQuestionImage = null;
    private boolean clearImageQuestion = false;

    //Used to store state.
    private Hashtable<String,String> currentStateQuestion = new Hashtable<String,String>();
    protected ActivityResultLauncher<String> contract;

    private ConstraintLayout cLayout;
    QuestionAcknowledgement questionModelBeforeChange = null;
    Integer timeout;
    boolean isNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        questionModelBeforeChange = new QuestionAcknowledgement();
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

        this.rootView = inflater.inflate(R.layout.fragment_question_admin_ack, container, false);
        long qid  = getArguments().getLong("question_id", -1);
        long sid  = getArguments().getLong("section_id", -1);
          isNew  = getArguments().getBoolean("is_new", false);

        btnImageBrowse = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminAckImageBrowse);
        btnClearImage = this.rootView.findViewById(R.id.buttonFragmentQuestionAdminAckImageClear);
        imgQuestion = this.rootView.findViewById(R.id.imageViewFragmentQuestionAdminAck);

        eTitle = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminAckTitle);
        eComment = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminAckComment);
        eSeq = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminAckSeq);
        swActive = this.rootView.findViewById(R.id.switchFragmentQuestionAdminAckActive);
        swMachEnbled = this.rootView.findViewById(R.id.switchFragmentQuestionAdminAckMachEnabled);
        eTimeout = this.rootView.findViewById(R.id.editTextFragmentQuestionAdminAckTimeout);

        this.viewModel = new ViewModelFragmentQuestionAdminAck(this.getActivity().getApplication(),qid , sid, isNew);

        btnImageBrowse.setOnClickListener(this);
        btnClearImage.setOnClickListener(this);
        cLayout = this.rootView.findViewById(R.id.constraintLayoutQuestionAdminAck);
        txtTimeoutLabel = rootView.findViewById(R.id.textViewFragmentQuestionAdminAckTimeout);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //The machine cannot be operating when asking an acknowledgment type question.
        //Hide entry controls.
        swMachEnbled.setVisibility(View.INVISIBLE);
        eTimeout.setVisibility(View.INVISIBLE);
        txtTimeoutLabel.setVisibility(View.INVISIBLE);

        //TODO: The image selection should be changed to a dialog and NOT navigate
        //away from the task at hand.
        if (this.viewModel.getQuestion() != null)
        {
            QuestionAcknowledgement question = (QuestionAcknowledgement)this.viewModel.getQuestion();
             saveOriginalQuestionModelValues(question);
            Question.loadResources(question, getActivity().getApplicationContext());

            if (Session.getInstance().hasPendingImageSelection())
            {
                restoreState();

                ParameterImageSelect parameterImageSelect = Session.getInstance().getLastImageSelectionParameters();
                if (parameterImageSelect.getImageIdx() == 1) {

                    imageQuestionUri = parameterImageSelect.getUri();
                    bmQuestionImage = parameterImageSelect.getThumbnail();
                    clearImageQuestion = false;
                }

            }
            else
            {
                eTitle.setText(question.getTitle());
                eSeq.setText(this.viewModel.isNew ?  Integer.toString(this.viewModel.getNextQuestionSequence()) : Integer.toString(question.getSequence()));
                swActive.setChecked(question.getEnabled());
                //swMachEnbled.setChecked(question.getAllowMachineOperation());
                //eTimeout.setText(Integer.toString(question.getTimeout()));
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

    private void saveOriginalQuestionModelValues(QuestionAcknowledgement question) {

        questionModelBeforeChange.setTitle(question.getTitle());
        questionModelBeforeChange.setComment(question.getComment());
        questionModelBeforeChange.setSequence(question.getSequence());
        questionModelBeforeChange.setEnabled(question.getEnabled());
        questionModelBeforeChange.setTimeout(question.getTimeout());
        questionModelBeforeChange.setAllowMachineOperation(question.getAllowMachineOperation());

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
        if (questionModelBeforeChange.getTimeout() != timeout) {
            AppLog.getInstance().updateValuesChangeString(
                    "Question Timeout (mins)",
                    String.valueOf(questionModelBeforeChange.getTimeout()),
                    String.valueOf(timeout));
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
        //For the setting that dictates if the machine is operating during questioning,
        // add a log entry if it was changed.
        if (!questionModelBeforeChange.getAllowMachineOperation() == swMachEnbled.isChecked()) {
            AppLog.getInstance().updateValuesChangeString(
                    "Machine enabled",
                    String.valueOf(questionModelBeforeChange.getAllowMachineOperation()),
                    String.valueOf(swMachEnbled.isChecked()));
            AppLog.getInstance().isValueUpdated = true;
        }
        // log changes
        if (AppLog.getInstance().isValueUpdated) {
            AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                    (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                    ActionsEnum.EVENT_SUPERVISOR_UPDATE_ADMIN_ACK_TYPE_QUESTION_ACTION.name(),
                    ResultEnum.RESULT_SUCCESS.name());
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
                FragmentQuestionAdminAck.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminAck.this);
                        navController.popBackStack();

                    }
                });
            }
        };

        switch(item.getItemId())
        {
            case R.id.action_menu_item_common_done:
            {
                //Hide keyboard.
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                View fview = cLayout.findFocus();
                if (fview != null)
                    imm.hideSoftInputFromWindow(cLayout.findFocus().getWindowToken(), 0);

                if (!validateInputs()) return true;

                  timeout = UIValidator.safeGetInteger(eTimeout);

                if (timeout == null)
                {
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
                        swMachEnbled.isChecked(),
                        timeout,
                        eComment.getText().toString().trim(),
                        runnable );

                if (!isNew) {
                    reportQuestionChanges();
                } else {
                    AppLog.getInstance().reportEvent(
                            Session.getInstance().getUser().getFullName(),
                            (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_NEW_ADMIN_ACK_TYPE_QUESTION_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());
                }
                return true;
            }
            case R.id.action_menu_item_common_delete:
            {
                Question.delete(this.getActivity().getApplication(), this.viewModel.getQuestion().getId(),
                        new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final NavController navController = NavHostFragment.findNavController(FragmentQuestionAdminAck.this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentQuestionAdminAckImageBrowse)
        {
            if (FragmentQuestionAdminHelper.checkPermissions(this, contract))
            {
                navigateToPhotoSelection();
            }
        }
        else if (v.getId() == R.id.buttonFragmentQuestionAdminAckImageClear)
        {
            clearImageQuestion  = true;
            imgQuestion.setImageBitmap(null);
            imageQuestionUri = "";
            bmQuestionImage = null;
        }
    }

    private boolean validateInputs()
    {
        try {

            UIValidator.checkTextEntry(eTitle, this.getView(), R.string.question_title_empty_message);
            UIValidator.checkTextEntry(eSeq, this.getView(), R.string.question_invalid_number);
            UIValidator.checkNumberEntry(eSeq, this.getView(), R.string.question_invalid_number );

            return true;


        }
        catch (UIFormatException fex)
        {
            //TODO : give focus to control
            fex.getView().requestFocus();
        }

        return false;
    }


    private void saveState()
    {
        try {
            currentStateQuestion.clear();
            currentStateQuestion.put("title", eTitle.getText().toString());
            currentStateQuestion.put("question_seq", eSeq.getText().toString());
            currentStateQuestion.put("active", Boolean.toString(swActive.isChecked()));
            currentStateQuestion.put("machine_enabled", Boolean.toString(swMachEnbled.isChecked()));
            currentStateQuestion.put("time_out", eTimeout.getText().toString());
            currentStateQuestion.put("comment", eComment.getText().toString());
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }

    private void restoreState() {
        try {
            eTitle.setText(currentStateQuestion.get("title"));
            eSeq.setText(currentStateQuestion.get("question_seq"));
            swActive.setChecked(Boolean.parseBoolean(currentStateQuestion.get("active")));
            swMachEnbled.setChecked(Boolean.parseBoolean(currentStateQuestion.get("machine_enabled")));
            eTimeout.setText(currentStateQuestion.get("time_out"));
            eComment.setText(currentStateQuestion.get("comment"));


        } catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    private void navigateToPhotoSelection()
    {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("section_title", "Select image for question");
            bundle.putLong("question_id", this.viewModel.question.getId());
            bundle.putInt("image_index", 1);

            saveState();
            Session.getInstance().clearLastParameterImageSelection();

            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_image_select, bundle);

        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }
    }


    public  void askForPermission()
    {
        try {
            if (contract!= null) contract.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }


}
