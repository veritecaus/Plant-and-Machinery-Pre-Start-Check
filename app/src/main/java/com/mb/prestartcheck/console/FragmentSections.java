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
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

import org.jetbrains.annotations.NotNull;

public class FragmentSections extends Fragment implements AdaptorSections.onSelectionSelectedListener {

    private View rootView;
    private ViewModelFragmentSections viewModel;
    private volatile AdaptorSections adaptorSections;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sections, container, false);
        this.viewModel = new ViewModelFragmentSections(this.getActivity().getApplication());
        this.viewModel.sync();

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewFragmentSections);
        adaptorSections = new AdaptorSections(this.viewModel.getSections(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adaptorSections);

         return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i(App.TAG, "FragmentSections started,");
        Questioner.getInstance().restart(true);

        this.viewModel.getSectionsInstance().addListener(this.getContext(), new Sections.onSyncListener()
        {

            @Override
            public void syncCompleted() {
                //Sync with singleton.
                Log.i(App.TAG, "'FragmentSections' syncing with the application Sectionsinstance.");

                FragmentSections.this.viewModel.sync();
                FragmentSections.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (FragmentSections.this.adaptorSections) {
                            FragmentSections.this.adaptorSections.notifyDataSetChanged();
                        }
                    }
                });
            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.getSectionsInstance().removeListener(this.getContext());
    }

    @Override
    public void onClick(Section e) {
        boolean isNewItem =e.equals(Section.getNewItem());

        final NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle= new Bundle();
        bundle.putLong("section_id",isNewItem ? 0: e.getId() );
        String title = isNewItem ? "New Section" : String.format("Edit - %s", e.getTitle());
        bundle.putString("title", title );

        navController.navigate(R.id.nav_section_edit, bundle);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_admin_section, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.action_menu_item_admin_section_add)
        {
            onClick(Section.getNewItem());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
