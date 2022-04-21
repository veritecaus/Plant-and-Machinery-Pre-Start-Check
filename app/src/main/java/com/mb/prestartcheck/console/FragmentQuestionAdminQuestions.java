package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionAcknowledgement;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.QuestionNew;
import com.mb.prestartcheck.QuestionOptions;
import com.mb.prestartcheck.QuestionYesNo;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;

import org.jetbrains.annotations.NotNull;

public class FragmentQuestionAdminQuestions extends Fragment implements AdaptorQuestions.onSelectionSelectedListener {

    private View rootView;
    private ViewModelQuestionAdminQuestions viewModel;
    private AdaptorQuestions adaptorQuestions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        long sectionId = getArguments().getLong("section_id", -1);

        this.viewModel = new ViewModelQuestionAdminQuestions(this.getActivity().getApplication(), sectionId);

        this.rootView = inflater.inflate(R.layout.fragment_question_admin_questions, container, false);

        RecyclerView recyclerView = this.rootView.findViewById(R.id.recyclerviewFragmentQuestionAdminQuestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptorQuestions = new AdaptorQuestions(this.viewModel.getQuestions(), this);
        recyclerView.setAdapter(this.adaptorQuestions);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Questions.getInstance().addListener(this.getContext(), new Questions.onSyncListener() {

            @Override
            public void syncCompleted() {
                //Sync with singleton.
                Log.i(App.TAG, "'FragmentQuestionAdminQuestions' syncing with the application Questions instance.");

                FragmentQuestionAdminQuestions.this.viewModel.sync();
                FragmentQuestionAdminQuestions.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (FragmentQuestionAdminQuestions.this.adaptorQuestions) {
                            FragmentQuestionAdminQuestions.this.adaptorQuestions.notifyDataSetChanged();
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Questions.getInstance().removeListener(this.getContext());
        Log.i(App.TAG, "'FragmentQuestionAdminQuestions' removed itself from listening to Question changes.");

    }

    @Override
    public void onClick(Question e) {

        Bundle bundle = new Bundle();
        bundle.putLong("question_id", e.getId());
        bundle.putLong("section_id", this.viewModel.getSection().getId());


        Session.getInstance().clearLastParameterImageSelection();

        if (e.getClass() == QuestionYesNo.class) {
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_question_admin_yesno, bundle);
        } else if (e.getClass() == QuestionMultipleChoice.class) {
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_question_admin_mul, bundle);

        } else if (e.getClass() == QuestionOptions.class) {
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_question_admin_options, bundle);

        } else if (e.getClass() == QuestionAcknowledgement.class) {
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_question_admin_ack, bundle);

        } else if (e.getClass() == QuestionNew.class) {
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_question_type, bundle);

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_admin_questions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.action_menu_item_questions_add) {
            onClick(QuestionNew.getInstance());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
