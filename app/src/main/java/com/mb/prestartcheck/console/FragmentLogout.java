package com.mb.prestartcheck.console;

import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.DateFormatScreen;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;
import com.mb.prestartcheck.Session;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentLogout extends Fragment  implements View.OnClickListener,
        ViewModelFragmentLogout.OnLogoutCompletedHandler {

    private View rootView;
    private ViewModelFragmentLogout viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        this.viewModel = new ViewModelFragmentLogout(this.getActivity().getApplication(), this);

        TextView tvFullname = rootView.findViewById(R.id.textViewFragmentLogoutFullName);
        tvFullname.setText(Questioner.getInstance().getQuestionerState().getUser().getFullName());

        TextView tvLogginDateTime = rootView.findViewById(R.id.textViewFragmentLogoutLogginDate);
        DateFormatScreen dateFormatScreen = new DateFormatScreen();
        tvLogginDateTime.setText(dateFormatScreen.format(Questioner.getInstance().getQuestionerState().getUser().getLastLogin()));

        TextView tvMachineName = rootView.findViewById(R.id.textViewFragmentLogoutMachineName);
        tvMachineName.setText(String.format("Machine: %s", Machine.getInstance().getName()));

        Button btnLogout = rootView.findViewById(R.id.buttonFragmentLogoutLogout);
        btnLogout.setOnClickListener(this);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
            //If the user has reached this point, the questioning is over and
            //liabilities resolved, then restart the question process
            viewModel.questioningCompleted();

    }

    @Override
    public void onPause() {
        AppLog.getInstance().print("FragmentLogout::OnPause.");
        Session.getInstance().stopInterlockDeviceMonitoring();
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        //Logout.
        Session.getInstance().stopInterlockDeviceMonitoring();
        this.viewModel.logout();

    }

    @Override
    public void loggedOut() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final NavController navController = NavHostFragment.findNavController(FragmentLogout.this);

                 NavDirections nav = FragmentLogoutDirections.actionNavLogoutToNavHome();
                navController.navigate(nav);


            }
        });
    }

}
