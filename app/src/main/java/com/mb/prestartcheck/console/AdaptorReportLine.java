package com.mb.prestartcheck.console;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.R;
import com.mb.prestartcheck.ReportLine;

import java.util.ArrayList;
import java.util.List;

public class AdaptorReportLine extends RecyclerView.Adapter<AdaptorReportLine.ViewHolder> {


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private  TextView tvSection;
        private  TextView tvQuestion;
        private  TextView tvOpResp;
        private  TextView tvExResp;
        private ImageView img;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            tvSection = itemView.findViewById(R.id.textViewContentReportLineSection);
            tvQuestion = itemView.findViewById(R.id.textViewContentReportLineQuestion);
            tvOpResp = itemView.findViewById(R.id.textViewContentReportLineOpResp);
            tvExResp = itemView.findViewById(R.id.textViewContentReportLineExResp);
            img = itemView.findViewById(R.id.imageViewContentReportLine);
        }

        public TextView  getTextViewSection()  { return this.tvSection;}
        public TextView  getTextViewQuestion()  { return this.tvQuestion;}
        public TextView  getTextViewOpResp()  { return this.tvOpResp;}
        public TextView  getTextViewExResp()  { return this.tvExResp;}
        public ImageView  getImage()  { return this.img;}

    }

    final List<ReportLine> report;

    public AdaptorReportLine(List<ReportLine> reportlines)
    {
        this.report = reportlines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_report_line, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorReportLine.ViewHolder holder, int position) {

        if (position < 0 || position >= report.size()) return;

        ReportLine reportLine = report.get(position);
        if (reportLine != null)
        {
            holder.getTextViewSection().setText(String.format("Section: %s", reportLine.getSectionTitle()));
            holder.getTextViewQuestion().setText(String.format("Question: %s", reportLine.getQuestionTitle()));
            holder.getTextViewOpResp().setText(reportLine.getOperatorResponse());
            holder.getTextViewExResp().setText(reportLine.getExpectedResponse());

            // TODO: 14/07/2021 Set image.
        }
    }

    @Override
    public int getItemCount() {
        return this.report.size();
    }

}
