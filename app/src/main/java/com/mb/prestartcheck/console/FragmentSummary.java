package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;


public class FragmentSummary extends Fragment implements View.OnClickListener,
        ViewModelFragmentLogout.OnLogoutCompletedHandler {

    private ViewModelFragmentSummary viewModel;
    private View rootView;
    private Button btnAccept, btnLogoutOperator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        btnAccept = this.rootView.findViewById(R.id.buttonFragmentSummaryAccept);
        btnLogoutOperator = this.rootView.findViewById(R.id.buttonFragmentSummaryLogOutOperator);

        viewModel = new ViewModelFragmentSummary(getActivity().getApplication(), this);

        AdaptorReportLine adaptorReportLine = new AdaptorReportLine(viewModel.getSummary());
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewFragmentSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptorReportLine);

        btnAccept.setOnClickListener(this);
        btnLogoutOperator.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFragmentSummaryAccept) {

            //Popup supervisor login screen.
            final FragmentSummaryDirections.ActionNavSummaryToNavLoginSupervisor actionNavSummaryToNavLoginSupervisor
                    = FragmentSummaryDirections.actionNavSummaryToNavLoginSupervisor();
            actionNavSummaryToNavLoginSupervisor.setShowRiskNotice(false);
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(actionNavSummaryToNavLoginSupervisor);
        }
        if (v.getId() == R.id.buttonFragmentSummaryLogOutOperator) {
            setBtnLogoutOperator();

        }

    }

    public void setBtnLogoutOperator() {
        try {

            this.viewModel.logout();

        } catch (Exception ex) {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    @Override
    public void loggedOut() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().getFragmentManager().popBackStack();
                final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_home);

            }
        });
    }
}