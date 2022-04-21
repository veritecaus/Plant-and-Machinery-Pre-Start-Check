package com.mb.prestartcheck.console;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.R;

import java.util.ArrayList;

public class AdatorSettingsEmailReportAdmin extends RecyclerView.Adapter<AdatorSettingsEmailReportAdmin.ViewHolder> {

    private ArrayList<String> items = new ArrayList<String>();
    private OnClickHandler clickHandler;

    public interface OnClickHandler
    {
        void onSettingsEmailReportAdminItemSelected(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {
        private final TextView textViewTite;
        private final ConstraintLayout constraintLayoutContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTite = itemView.findViewById(R.id.textViewGenericRecyclerViewItem);
            constraintLayoutContainer =  itemView.findViewById(R.id.constraintlayoutGenericRecyclerViewItem);

            itemView.setOnClickListener(this);
        }

        public TextView getTextViewTite() { return this.textViewTite;}
        public ConstraintLayout getConstraintLayoutContainer() { return this.constraintLayoutContainer;}

        @Override
        public void onClick(View v) {
            if (clickHandler != null) clickHandler.onSettingsEmailReportAdminItemSelected(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_generic_recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdatorSettingsEmailReportAdmin.ViewHolder holder, int position) {
        holder.getTextViewTite().setText(items.get(position));

    }

    public  AdatorSettingsEmailReportAdmin(String[] values, OnClickHandler onClickHandler)
    {
        this.clickHandler = onClickHandler;

        for(String s : values)
            items.add(s);

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

}
