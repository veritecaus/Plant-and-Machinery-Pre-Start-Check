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

import java.util.ArrayList;

public class AdaptorMultipleChoice extends RecyclerView.Adapter<AdaptorMultipleChoice.ViewHolder> {

    final ArrayList<String> items;

    public  interface  OnMultipleChoiceItemClickListener
    {
        public void onItemSelected(String s);
    }

    private OnMultipleChoiceItemClickListener clickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener  {
        final TextView tvTitle;
        final ConstraintLayout constraintLayout;

        public ViewHolder(View view)
        {
            super(view);
            view.setOnClickListener(this);
            tvTitle =  view.findViewById(R.id.textViewMultipleChoiceItemTitle);
            constraintLayout =  view.findViewById(R.id.constraintLayoutMultipleChoiceItemContainer);

        }

        public TextView getTextViewTitle() { return this.tvTitle;}
        public ConstraintLayout getConstraintLayout() { return this.constraintLayout;}

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemSelected(items.get(getAdapterPosition()));
        }
    }


    public AdaptorMultipleChoice(ArrayList<String> i,OnMultipleChoiceItemClickListener listener )
    {
        this.clickListener = listener;
        this.items = i;
    }

    @NonNull
    @Override
    public AdaptorMultipleChoice.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_multiple_choice_item, parent, false);

        return new AdaptorMultipleChoice.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorMultipleChoice.ViewHolder holder, int position) {

        holder.tvTitle.setText(this.items.get(position));
        /*
        if (position % 2 ==0 )
            holder.getConstraintLayout().setBackgroundColor(Color.LTGRAY);
        else
            holder.getConstraintLayout().setBackgroundColor(Color.WHITE);
*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
