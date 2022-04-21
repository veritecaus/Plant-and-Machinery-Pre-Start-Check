package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

public class FragmentQuestionAdminStart extends Fragment implements  AdaptorSections.onSelectionSelectedListener {

    private View rootView;
    private AdaptorSections adaptorSections;
    private ViewModelFragmentQuestionAdminStart viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_question_admin_start, container, false);
        viewModel = new ViewModelFragmentQuestionAdminStart(this.getActivity().getApplication());

        RecyclerView recyclerView  = rootView.findViewById(R.id.recyclerViewFragmentQuestionAdminStart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adaptorSections = new AdaptorSections(this.viewModel.getSections(), this);
        recyclerView.setAdapter(adaptorSections);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i(App.TAG, "FragmentQuestionAdminStart started,");
        Questioner.getInstance().restart(true);

        this.viewModel.getSections().addListener(this.getContext(), new Sections.onSyncListener()
        {

            @Override
            public void syncCompleted() {
                //Sync with singleton.
                Log.i(App.TAG, "'FragmentSections' syncing with the application Sectionsinstance.");

                FragmentQuestionAdminStart.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (FragmentQuestionAdminStart.this.adaptorSections) {
                            FragmentQuestionAdminStart.this.adaptorSections.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.getSections().removeListener(this.getContext());
    }

    @Override
    public void onClick(Section e) {
        //Navigate to the add operator fragment.
        NavController navController = NavHostFragment.findNavController(this);

        FragmentQuestionAdminStartDirections.ActionQuestionAdminStartToQuestions action =
                FragmentQuestionAdminStartDirections.actionQuestionAdminStartToQuestions();
        action.setSectionId(e.getId());
        action.setSectionTitle(String.format("%s - Questions", e.getTitle()));

        navController.navigate(action);

    }
}
