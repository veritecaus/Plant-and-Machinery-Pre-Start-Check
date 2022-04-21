package com.mb.prestartcheck.console;

import android.app.PendingIntent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;

public class FragmentSettingsUsers extends Fragment implements   AdaptorUsers.OnUserClickListener   {

    private ViewModelFragmentSettingsUsers viewModel;
    private View rootView;
    private AdaptorUsers adaptorUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_settings_users, container, false);
        this.viewModel = new ViewModelFragmentSettingsUsers(getActivity().getApplication());

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewFragmentSettingsUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptorUsers = new AdaptorUsers(Operators.getInstance(), Supervisors.getInstance(), this, true);

        recyclerView.setAdapter(adaptorUsers);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(App.TAG, "FragmentSettingsUsers started,");

        //The user could be answering questions before navigating here, so pause
        //the questioning.
        Questioner.getInstance().pause();

        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    FragmentSettingsUsers.this.adaptorUsers.buildUserList(FragmentSettingsUsers.this.viewModel.getOperators(),
                            FragmentSettingsUsers.this.viewModel.getSupervisors(), true);
                    //Notify the recycler view of the change.
                    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewFragmentSettingsUsers);
                    recyclerView.getAdapter().notifyDataSetChanged();


                }
            };

            viewModel.getOperators().addListener(this.getContext(), new Operators.onSyncListener() {
                @Override
                public void syncCompleted() {
                    AppLog.getInstance().print("FragmentSettingsUsers refreshed it's list. Operators changed.");
                    r.run();
                }
            });

            viewModel.getSupervisors().addListener(this.getContext(), new Supervisors.onSyncListener() {
                @Override
                public void syncCompleted() {
                    AppLog.getInstance().print("FragmentSettingsUsers refreshed it's list. Supervisors changed.");
                    r.run();

                }
            });


        }
        catch(Exception ex)
        {
            Log.e("prestartcheck", ex.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.getOperators().removeListener(this.getContext());
        viewModel.getSupervisors().removeListener(this.getContext());
    }

    @Override
    public void onUserSelected(User e) {
        try {

            final FragmentSettingsUsersDirections.ActionNavSettingUsersToNavUserAdd navAction =
                    FragmentSettingsUsersDirections.actionNavSettingUsersToNavUserAdd(e == null ? -1 : e.getId());
            final NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(navAction);

        }
        catch (Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_operators, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull  Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean isSuper = Session.getInstance().isSupervisorLoggedIn();
        menu.findItem(R.id.menu_item_show_users_add_user).setVisible(isSuper);
        menu.findItem(R.id.menu_item_show_users_superversor_logout).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_show_users_add_user)
        {
            onUserSelected(null);
            return true;
        }

        return false;
    }




}