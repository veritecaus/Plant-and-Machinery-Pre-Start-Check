package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.fins.ActionsEnum;
import com.mb.prestartcheck.fins.ResultEnum;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentSectionEdit extends Fragment {

    private View rootView;
    private ViewModelFragmentSectionEdit viewModel;
    String title = "";
    String description = "";
    int sectionNumber;
    boolean showCover;
    boolean randomize;
    boolean active;
    boolean addNewSection = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_section_edit, container, false);
        final long sid = getArguments().getLong("section_id", 0);
        this.viewModel = new ViewModelFragmentSectionEdit(this.getActivity().getApplication(), sid);
        String addNewSectionValue = getArguments().getString("title");
        if (addNewSectionValue.equalsIgnoreCase("New Section")) {
            addNewSection = true;
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        EditText etTitle = this.rootView.findViewById(R.id.editTextFragmentSectionEditTitle);
        EditText etDesc = this.rootView.findViewById(R.id.editTextFragmentSectionEdtDesc);
        EditText etNumber = this.rootView.findViewById(R.id.editTextFragmentSectionEditNumber);
        //editTextFragmentSectionEditNumber
        Switch sShowCover = this.rootView.findViewById(R.id.switchFragmentSectionEditShowCover);
        Switch sRandomize = this.rootView.findViewById(R.id.switchFragmentSectionEditRandomize);
        Switch sActive = this.rootView.findViewById(R.id.switchFragmentSectionEditActive);

        Section section = this.viewModel.getSection();
        title = section.getTitle();
        description = section.getDescription();
        sectionNumber = section.getSequence();
        showCover = section.getShowCoverPage();
        randomize = section.getRandomQuestions();
        active = section.getEnabled();
        Log.i("updatedQuestionModel", title);

        etTitle.setText(section.getTitle());
        etDesc.setText(section.getDescription());
        etNumber.setText(String.format("%d", section.getSequence()));

        sShowCover.setChecked(section.getShowCoverPage());
        sRandomize.setChecked(section.getRandomQuestions());
        sActive.setChecked(section.getEnabled());

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_common_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                FragmentSectionEdit.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NavController navController = NavHostFragment.findNavController(FragmentSectionEdit.this);
                        navController.popBackStack();
                    }
                });
            }
        };
        switch (item.getItemId()) {
            case R.id.action_menu_item_common_done: {

                if (!validateInputs()) return true;

                if (addNewSection) {

                    setSectionValues(r);
                    AppLog.getInstance().reportEvent(Session.getInstance().getUser().getFullName(), (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                            ActionsEnum.EVENT_SUPERVISOR_ADD_SECTION_ACTION.name(),
                            ResultEnum.RESULT_SUCCESS.name());

                } else {

                    setSectionValues(r);
// check to identify which fields were changed on this
                    if (!title.equals(viewModel.getSection().getTitle())) {
                        AppLog.getInstance().updateValuesChangeString(
                                "Title",
                                title,
                                viewModel.getSection().getTitle());
                        AppLog.getInstance().isValueUpdated = true;
                    }
                    if (!description.equals(viewModel.getSection().getDescription())) {
                        AppLog.getInstance().updateValuesChangeString(
                                "description",
                                description,
                                viewModel.getSection().getDescription());
                        AppLog.getInstance().isValueUpdated = true;
                    }
                    if (sectionNumber != viewModel.getSection().getSequence()) {
                        AppLog.getInstance().updateValuesChangeString(
                                "Section Number",
                                String.valueOf(sectionNumber),
                                String.valueOf(viewModel.getSection().getSequence()));
                        AppLog.getInstance().isValueUpdated = true;
                    }
                    if (showCover != viewModel.getSection().getShowCoverPage()) {
                        AppLog.getInstance().updateValuesChangeString(
                                "Show description as Cover Page ?",
                                String.valueOf(showCover),
                                String.valueOf(viewModel.getSection().getShowCoverPage()));
                        AppLog.getInstance().isValueUpdated = true;
                    }
                    if (randomize != viewModel.getSection().getRandomQuestions()) {
                        AppLog.getInstance().updateValuesChangeString(
                                "Randomize questions in this section ?",
                                String.valueOf(randomize),
                                String.valueOf(viewModel.getSection().getRandomQuestions()));
                        AppLog.getInstance().isValueUpdated = true;
                    }
                    if (active != viewModel.getSection().getEnabled()) {
                        AppLog.getInstance().updateValuesChangeString(
                                "Active ?",
                                String.valueOf(active),
                                String.valueOf(viewModel.getSection().getEnabled()));
                        AppLog.getInstance().isValueUpdated = true;
                    }

                    // log changes
                    if (AppLog.getInstance().isValueUpdated) {
                        AppLog.getInstance().reportValuesChangeEvent(Session.getInstance().getUser().getFullName(),
                                (new SimpleDateFormat("HH:mm a, dd/MM/yyyy", Locale.getDefault()).format(new Date())),
                                ActionsEnum.EVENT_SUPERVISOR_UPDATE_SECTION_ACTION.name(),
                                ResultEnum.RESULT_SUCCESS.name());
                    }
                }
                return true;
            }
            case R.id.action_menu_item_common_delete: {
                this.viewModel.delete(r);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setSectionValues(Runnable r) {
        EditText etTitle = this.rootView.findViewById(R.id.editTextFragmentSectionEditTitle);
        EditText etDesc = this.rootView.findViewById(R.id.editTextFragmentSectionEdtDesc);
        EditText etNumber = this.rootView.findViewById(R.id.editTextFragmentSectionEditNumber);
        Switch sShowCover = this.rootView.findViewById(R.id.switchFragmentSectionEditShowCover);
        Switch sRandomize = this.rootView.findViewById(R.id.switchFragmentSectionEditRandomize);
        Switch sActive = this.rootView.findViewById(R.id.switchFragmentSectionEditActive);

        this.viewModel.setSection(etTitle.getText().toString().trim(),
                etDesc.getText().toString().trim(),
                Integer.parseInt(etNumber.getText().toString().trim()),
                sShowCover.isChecked(),
                sRandomize.isChecked(),
                sActive.isChecked(),
                r);
    }


    private boolean validateInputs() {
        try {

            UIValidator.checkTextEntry(this.rootView.findViewById(R.id.editTextFragmentSectionEditTitle),
                    this.getView(), R.string.section_title_empty_message);


            UIValidator.checkNumberEntry(this.rootView.findViewById(R.id.editTextFragmentSectionEditNumber),
                    this.getView(), R.string.section_invalid_sequence);


            return true;


        } catch (UIFormatException fex) {
            //TODO : give focus to control
        }

        return false;
    }

}


