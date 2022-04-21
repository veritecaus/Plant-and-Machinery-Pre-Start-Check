package com.mb.prestartcheck.console;


import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mb.prestartcheck.App;
import com.mb.prestartcheck.DateFormatScreen;
import com.mb.prestartcheck.DateFormaterLog;
import com.mb.prestartcheck.Machine;
import com.mb.prestartcheck.Operator;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.User;
import com.mb.prestartcheck.data.PrestartCheckDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentLogin extends Fragment implements View.OnClickListener{

    View rootView;
    ViewModelFragmentLogin viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        long userId  = getArguments().getLong("user_id");

        viewModel = new ViewModelFragmentLogin(getActivity().getApplication(), userId);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        User user = viewModel.getUser();

        TextView tvLoginCaption = rootView.findViewById(R.id.textViewFragmentLoginWelcome);
        tvLoginCaption.setText(String.format("Welcome %s", user == null ? "" : user.getFullName()));

        TextView tvLoginDate = rootView.findViewById(R.id.textViewFragmentLoginDate);
        Date today = new Date();
        DateFormatScreen df = new DateFormatScreen();
        tvLoginDate.setText(String.format("%s", df.format(today)));

        TextView tvLoginMachine = rootView.findViewById(R.id.textViewFragmentLoginMachine);
        tvLoginMachine.setText(String.format("Machine %s", Machine.getInstance().getName()));

        Button bLogin =  rootView.findViewById(R.id.buttonFragmentLogin);

        bLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        EditText editTextPin = rootView.findViewById(R.id.editTextFragmentLoginPIN);
        //Hide the on screen keyboard.
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPin.getWindowToken(), 0);

        if (v.getId() == R.id.buttonFragmentLogin)
        {

            User user =viewModel.getUser();


            if (User.isPinMatched(user, editTextPin.getText().toString().trim()))
            {
                user.setLastLogin(new Date());

                Log.i(App.TAG, String.format("%s logged in.", user.getFullName()));

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //Navigate back to the operators list, but the destination will start
                        //asking questions.
                        FragmentLogin.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                    NavController navController = NavHostFragment.findNavController(FragmentLogin.this);
                                    navController.popBackStack();

                            }
                        });

                    }
                };

                this.viewModel.login(r);
            }
            else
            {
                //Wrong password.
                editTextPin.setText("");
                editTextPin.requestFocus();

                Snackbar.make( getView(),  R.string.error_message_wrong_pin, Snackbar.LENGTH_LONG).show();

            }

        }
    }


}
