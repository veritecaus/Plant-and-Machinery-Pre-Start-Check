package com.mb.prestartcheck.console;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mb.prestartcheck.QuestionType;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;

public class FragmentQuestionAdminNew extends Fragment {

    private ViewModelFragmentQuestionAdminNew viewModel;
    private View rootView;
    private AdaptorQuestionType adaptorQuestionType;
    private long sectionId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_question_type, container, false);
        this.viewModel = new ViewModelFragmentQuestionAdminNew(getActivity().getApplication());
        this.sectionId = getArguments().getLong("section_id", -1);

        adaptorQuestionType = new AdaptorQuestionType(this.viewModel.getList(), new AdaptorQuestionType.AdaptorQuestionTypeListener() {
            @Override
            public void onQuestionTypePressed(QuestionType questionType) {
                navigateToQuestionPage(questionType);
            }
        });

        RecyclerView recyclerView = this.rootView.findViewById(R.id.recyclerViewFragmentQuestionType);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.adaptorQuestionType);

        return  this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void navigateToQuestionPage(QuestionType questionType)
    {

        final NavController navController = NavHostFragment.findNavController(this);
        int navId = -1;

        switch ((int)questionType.getId())
        {
            case 1:
            {
                navId = R.id.nav_question_admin_yesno;
                break;
            }
            case 2:
            {
                navId = R.id.nav_question_admin_mul;
                break;
            }
            case 3:
            {
                navId = R.id.nav_question_admin_options;
                break;
            }
            case 4:
            {
                navId = R.id.nav_question_admin_ack;
                break;
            }

        }

        Bundle bundle = new Bundle();
        bundle.putLong("question_id", -1);
        bundle.putLong("section_id", this.sectionId);
        bundle.putBoolean("is_new", true);

        Session.getInstance().clearLastParameterImageSelection();

        navController.navigate(navId, bundle);
    }
}