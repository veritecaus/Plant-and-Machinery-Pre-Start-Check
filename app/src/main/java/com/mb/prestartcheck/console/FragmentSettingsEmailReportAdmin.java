package com.mb.prestartcheck.console;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

public class FragmentSettingsEmailReportAdmin extends Fragment implements  AdatorSettingsEmailReportAdmin.OnClickHandler  {
    private View rootView;
    private ViewModelFragmentSettingsEmailReportAdmin viewModel;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_settings_email_report_admin, container,false);
        this.viewModel = new ViewModelFragmentSettingsEmailReportAdmin(getActivity().getApplication());
        RecyclerView recyclerView = this.rootView.findViewById(R.id.recyclerViewSettingEmailReportAdmin);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        String[] titles = getResources().getStringArray(R.array.email_report_admin_titles);
        AdatorSettingsEmailReportAdmin adaptor = new AdatorSettingsEmailReportAdmin(titles, this );
        recyclerView.setAdapter(adaptor);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(App.TAG, "FragmentSettingsEmailReportAdmin started,");

        //The user could be answering questions before navigating here, so pause
        //the questioning.
        Questioner.getInstance().pause();


    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onSettingsEmailReportAdminItemSelected(int position) {
        NavDirections dir = null;
        switch(position)
        {
            case 0:
            {
                dir = FragmentSettingsEmailReportAdminDirections.actionNavSettingsEmailReportAdminToNavSettingsEmailDetails();
                break;
            }
            case 1:
            {
                dir = FragmentSettingsEmailReportAdminDirections.actionNavSettingsEmailReportAdminToNavSettingsReportingOptions();
                break;
            }
            case 2:
            {
                dir = FragmentSettingsEmailReportAdminDirections.actionNavSettingsEmailReportAdminToNavSettingsBypassReportingOptions();
                break;
            }
        }

        final NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(dir);

    }
}
