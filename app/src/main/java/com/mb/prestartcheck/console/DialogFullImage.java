package com.mb.prestartcheck.console;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.R;

public class DialogFullImage extends DialogFragment {

    final private ImageLocal image;
    public DialogFullImage(ImageLocal img)
    {
        image = img;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.view_full_image, null);

        ImageView imageView = v.findViewById(R.id.imageViewViewFullImage);
        if (image.getDisplayImage() != null)
            imageView.setImageBitmap(image.getDisplayImage());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.dismiss();
                    }
                })


        .setView(v);
        return builder.create();
    }

}
