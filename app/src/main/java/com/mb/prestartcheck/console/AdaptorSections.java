package com.mb.prestartcheck.console;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

public class AdaptorSections extends RecyclerView.Adapter<AdaptorSections.ViewHolder> {
    final Sections sections ;

    public  interface onSelectionSelectedListener
    {
        public  void onClick(Section e);
    }

    onSelectionSelectedListener clickListener;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tvTitle;
        private final ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textViewViewSectionsTitle);
            constraintLayout = itemView.findViewById(R.id.constraintViewViewSections);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        clickListener.onClick(sections.getAt(getAdapterPosition()));
                }
            });
        }

         public TextView getTvTitle()  {  return tvTitle; }
         public  ConstraintLayout getConstrintLayout() {  return constraintLayout; }
    }


    public  AdaptorSections(Sections e, onSelectionSelectedListener l)
    {
        this.sections = e;
        this.clickListener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_sections, parent, false);

        return new AdaptorSections.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorSections.ViewHolder holder, int position) {
        Section section = this.sections.getAt(position);
        if (section != null)
        {
            holder.getTvTitle().setText(section.getTitle());
            //holder.getConstrintLayout().setBackgroundColor(position %2 == 0 ? Color.LTGRAY : Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return  this.sections.getSize();
    }

}
