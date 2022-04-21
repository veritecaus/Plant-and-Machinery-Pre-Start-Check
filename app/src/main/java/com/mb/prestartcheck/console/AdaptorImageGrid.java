package com.mb.prestartcheck.console;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.R;


import java.util.List;


public class AdaptorImageGrid extends RecyclerView.Adapter <AdaptorImageGrid.ViewHolder> {

    final private List<ImageLocal> images;
    final private AdaptorImageGridListener listener;

    public  interface  AdaptorImageGridListener
    {
        void onAdaptorImageGridSelection(ImageLocal selected);
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageViewViewImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onAdaptorImageGridSelection(images.get(getAdapterPosition()));
                }
            });
        }

        public ImageView getImageView() { return this.imageView;}
    }

    public AdaptorImageGrid(List<ImageLocal> list, AdaptorImageGridListener listener)
    {
        this.images = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image, parent, false);

        return new AdaptorImageGrid.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= images.size() || position < 0) return;

        ImageLocal img = images.get(position);
        holder.getImageView().setImageBitmap(img.getThumbNail());

    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
