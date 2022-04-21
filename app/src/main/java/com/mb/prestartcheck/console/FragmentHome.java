package com.mb.prestartcheck.console;

import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;


public class FragmentHome extends FragmentQuestion implements   AdaptorUsers.OnUserClickListener {
    private ViewModelFragmentHome viewModel;
    private View rootView;
    private AdaptorUsers adaptorUsers;
    private RecyclerView recyclerView;

    public FragmentHome(){

    }

    /**
     *  OnCreate override. View model created here.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelFragmentHome(this.getActivity().getApplication());
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //If specified in the view model, add a menu.
        setHasMenu();

        this.rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }

    /**
     * Save view references so it can be used later.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.recyclerView = rootView.findViewById(R.id.recyclerview_fragment_operators);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptorUsers = new AdaptorUsers(Operators.getInstance(), Supervisors.getInstance(), this);
        recyclerView.setAdapter(adaptorUsers);
    }

    @Override
    public void onUserSelected(User e) {
        try {

            //TODO: Use safe args navigation.
            FragmentHomeDirections.ActionNavHomeToNavLogin actionToLogin = FragmentHomeDirections.actionNavHomeToNavLogin();
            actionToLogin.setUserId(e.getId());

            final NavController navController = NavHostFragment.findNavController(this);

//            Bundle bundle = new Bundle();
//            bundle.putLong("user_id", e.getId());

            navController.navigate(actionToLogin);

        }
        catch(Exception ex)
        {
            Log.e(App.TAG, ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        AppLog.getInstance().print("FragmenHome onStartCalled .");

        ((MainActivity)getActivity()).secureNavigation();
        boolean isLoggedIn = Session.getInstance().getUser() != null;

        if (isLoggedIn)
        {
            /*************************************************
             * A user  has logged in.
             *************************************************/

            Questioner.QuestionState qState = Questioner.getInstance().getQuestionerState().getState();
            if (qState == Questioner.QuestionState.Paused || qState == Questioner.QuestionState.Finished)
            {
                AppLog.getInstance().print("Resume asking questions.");
                this.currentQuestion();
            }
            else
            {
                Log.i(App.TAG, "User loggin in. Start asking questions.");
                this.nextQuestion(this.viewModel);
            }
            return;
        }

        showUsers();

        //Ensure that the interlock is opened so that the operator must log in and
        //answer safty questions.
       // this.viewModel.checkInterlockDeviceIsOpen();

    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.getOperators().removeListener(this.getContext());
        viewModel.getSuperVisors().removeListener(this.getContext());
    }

    @Override
    protected boolean canOperateMachine() {
        return false;
    }


    private  void showUsers()
    {
        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    FragmentHome.this.adaptorUsers.buildUserList(FragmentHome.this.viewModel.getOperators(),
                            FragmentHome.this.viewModel.getSuperVisors(), false);
                    //Notify the recycler view of the change.

                    recyclerView.getAdapter().notifyDataSetChanged();


                }
            };

            viewModel.getOperators().addListener(this.getContext(), new Operators.onSyncListener() {
                @Override
                public void syncCompleted() {
                    Log.i(App.TAG, "FragmentShowOperators refreshed it's list. Operators changed.");
                    r.run();
                }
            });

            viewModel.getSuperVisors().addListener(this.getContext(), new Supervisors.onSyncListener() {
                @Override
                public void syncCompleted() {
                    Log.i(App.TAG, "FragmentShowOperators refreshed it's list. Supervisors changed.");
                    r.run();

                }
            });


        }
        catch(Exception ex)
        {
            Log.e("prestartcheck", ex.getMessage());
        }
    }



}
