package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;

import org.jetbrains.annotations.NotNull;

public class FragmentSectionCover extends FragmentQuestion implements  View.OnClickListener {

    View rootView;
    ViewModelSectionCover viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If specified in the view model, add a menu.
        setHasMenu();

        rootView = inflater.inflate(R.layout.fragment_section_cover, container, false);

        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        long sid = getArguments().getLong("section_id");
        long qid = getArguments().getLong("question_id", -1);

        Button buttonNext = this.rootView.findViewById(R.id.buttonFragmentSectionCoverNext);
        buttonNext.setOnClickListener(this);
        viewModel = new ViewModelSectionCover(getActivity().getApplication(), sid, qid);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(App.TAG, this.getClass().getName() + "onStart called.");

        Section s = this.viewModel.getSection();

        if (s != null) {
            TextView tvDesc =  rootView.findViewById(R.id.textViewFragmentSectionCoverDesc);
            TextView tvStatus =  rootView.findViewById(R.id.textViewFragmentSectionCoverStatus);

            tvDesc.setText(s.getDescription());
            int progress = this.viewModel.getQuestion() == null ? 0 : this.viewModel.getQuestion().getSequence();

            tvStatus.setText(String.format("Question %d of %d",  progress, s.getSize() ));
            this.setProgress(tvStatus);

        }
    }

    @Override
    protected boolean canOperateMachine() {
        return false;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        //This frgament has it's own menu.
        //Bypass parent's menu creation.
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cover_page, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_menu_cover_page_next:
            {
                this.nextQuestion(this.viewModel);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        this.nextQuestion(this.viewModel);
    }
}
