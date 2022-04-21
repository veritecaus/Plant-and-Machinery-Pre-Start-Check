package com.mb.prestartcheck.console;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.ParameterImageSelect;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Session;

import java.util.ArrayList;

public class FragmentImageSelect extends Fragment implements  AdaptorImageGrid.AdaptorImageGridListener {

    private ViewModelFragmentImageSelect viewModel;
    private View rootView;
    private RecyclerView recyclerView;
    private AdaptorImageGrid adaptorImageGrid;
    private long questionId = -1;
    private int imgIndex = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_image_select_fragment, container, false);
        viewModel = new ViewModelFragmentImageSelect(getActivity().getApplication());

        questionId = getArguments().getLong("question_id", -1);
        imgIndex     = getArguments().getInt("image_index", 1);

        this.recyclerView = this.rootView.findViewById(R.id.recyclerViewFragmentImageSelect);

        adaptorImageGrid = new AdaptorImageGrid(viewModel.getImageList(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adaptorImageGrid);


        return  this.rootView;
    }

    @Override
    public void onStart() {

        super.onStart();
        this.viewModel.searchForPhotos();
        this.adaptorImageGrid.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAdaptorImageGridSelection(ImageLocal selected) {
        Session.getInstance().setLastImageSelectionParameters(new ParameterImageSelect(selected.getUri().toString(),
                questionId, imgIndex, selected.getThumbNail()));


        final NavController navController = NavHostFragment.findNavController(FragmentImageSelect.this);
        navController.popBackStack();


/*
        if (questionId > 0)
            this.viewModel.setQuestionImage(getActivity().getApplicationContext(), questionId, selected, isAltImage, runnable );
        else
            this.viewModel.setCompanyLogo(selected, runnable );
*/

    }
}