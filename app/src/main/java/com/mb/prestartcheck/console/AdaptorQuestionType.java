package com.mb.prestartcheck.console;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.QuestionType;
import com.mb.prestartcheck.R;

import java.util.List;


public class AdaptorQuestionType extends RecyclerView.Adapter<AdaptorQuestionType.ViewHolder> {

    final private List<QuestionType> data;
    final private AdaptorQuestionTypeListener listener;

    public  interface AdaptorQuestionTypeListener {
        void onQuestionTypePressed(QuestionType questionType);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textView;
        private final ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewGenericRecyclerViewItem);
            constraintLayout = itemView.findViewById(R.id.constraintlayoutGenericRecyclerViewItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) listener.onQuestionTypePressed(data.get(getAdapterPosition()));
                }
            });

        }

        public TextView getTextView() { return this.textView;}
        public ConstraintLayout getConstraintLayout() { return this.constraintLayout;}

    }

    public  AdaptorQuestionType(@NonNull List<QuestionType> list, AdaptorQuestionTypeListener listener)
    {
        this.data = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_generic_recyclerview_item, parent, false);

        return new AdaptorQuestionType.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(data.get(position).getLabel());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

}
