package com.mb.prestartcheck.console;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Section;

import org.w3c.dom.Text;

public class AdaptorQuestions extends RecyclerView.Adapter<AdaptorQuestions.ViewHolder> {

    public  interface onSelectionSelectedListener
    {
        public  void onClick(Question e);
    }

    private final Questions questions;
    private final onSelectionSelectedListener listener;

    class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView tvText;
        private final ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvText = itemView.findViewById(R.id.textViewViewQuestionsTitle);
            this.constraintLayout = itemView.findViewById(R.id.constraintLayoutQuestionAdminQuestions);
            itemView.setOnClickListener(this);
        }

        public TextView getTvText() { return tvText;}
        public ConstraintLayout getConstraintLayout() { return constraintLayout;}

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onClick(questions.getAt(getAdapterPosition()));
        }
    }

    public AdaptorQuestions(Questions q, onSelectionSelectedListener l) {
        this.questions = q;
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_questions, parent, false);


        return new AdaptorQuestions.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull  AdaptorQuestions.ViewHolder holder, int position) {
        Question question = this.questions.getAt(position);
        if (question != null)
        {
            holder.getTvText().setText(question.toString());
        }
    }

    @Override
    public int getItemCount() {
        return this.questions.getSize();
    }


}
