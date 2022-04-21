package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Response;


public class FragmentRiskNotice extends Fragment implements View.OnClickListener {

    private View rootView;
    private ViewModelFragmentRiskNotice viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_risk_notice, container, false);
        viewModel = new ViewModelFragmentRiskNotice(getActivity().getApplication(),
                getArguments().getLong("supervisor_id", -1));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView textViewQuestionTitle = this.rootView.findViewById(R.id.textViewFragmentRiskNoticeQuestion);
        Question question = this.viewModel.getQuestion();
        Response response = this.viewModel.getReponse();

        if (question != null && response != null)
        {

            textViewQuestionTitle.setText(response.getIsNegative() ? question.getTitleAlternative() : question.getTitle());

            TextView textViewQuestionOpResp = this.rootView.findViewById(R.id.textViewFragmentRiskNoticeOpResp);
            TextView textViewQuestionExpecteResp = this.rootView.findViewById(R.id.textViewFragmentRiskNoticeExpectedResp);

            textViewQuestionOpResp.setText(response == null ? "" : response.getOperatorResponse());
            textViewQuestionExpecteResp.setText(question.getExpectedAnswer());
        }

        Button buttonAccept = rootView.findViewById(R.id.buttonFragmentRiskNoticeAccept);
        Button buttonReject = rootView.findViewById(R.id.buttonFragmentRiskNoticeReject);

        buttonAccept.setOnClickListener(this);
        buttonReject.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Question question = this.viewModel.getQuestion();
        Question section = this.viewModel.getQuestion();


        if (question != null && section != null)
        {
            boolean accepted = v.getId() == R.id.buttonFragmentRiskNoticeAccept;

            this.viewModel.manageRisk(accepted);

            final NavController navController = NavHostFragment.findNavController(this);
            Bundle bundle = new Bundle();
            bundle.putLong("question_id", question.getId());
            bundle.putBoolean("supervisor_bypass", accepted);
            bundle.putLong("section_id", section.getId());
            bundle.putString("section_title", section.getTitle());

            navController.navigate(Questioner.getInstance().getQuestionerState().getSavedNavDestination(), bundle);

        }



    }
}
