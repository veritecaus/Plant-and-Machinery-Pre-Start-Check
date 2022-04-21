package com.mb.prestartcheck.console;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.AppLog;
import com.mb.prestartcheck.ImageLocal;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.QuestionMultipleChoice;
import com.mb.prestartcheck.QuestionYesNo;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewModelFragmentImageSelect extends AndroidViewModel {
    private final ArrayList<ImageLocal> images = new ArrayList<ImageLocal>();
    public static final Uri locationForPhotos =  MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

    private long questionId;
    public ViewModelFragmentImageSelect(@NonNull Application application) {
        super(application);

    }


    public List<ImageLocal> getImageList() { return this.images;}

    public int searchForPhotos()
    {
        int noPhotos = 0;

        this.images.clear();

        try {
            //Get a list of image name on the device.

            Cursor cursor = getApplication().getApplicationContext().getContentResolver().query(
                    locationForPhotos,
                    null,
                    null,
                    null,
                    null);
            while(cursor.moveToNext())
            {
                int idxDisplayName = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                int idxID = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);

                String imageName = cursor.getString(idxDisplayName);
                long id = cursor.getLong(idxID);

                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                // Load thumbnail of a specific media item.
                Bitmap thumbnail =
                        getApplication().getApplicationContext().getContentResolver().loadThumbnail(
                                imageUri, new Size(640, 480), null);

                images.add(new ImageLocal(imageUri, thumbnail ));

            }
            cursor.close();
        }
        catch(Exception ex)
        {
            AppLog.getInstance().print(ex.getMessage());
        }

        return 0;
    }

    public  void setCompanyLogo(ImageLocal imageLocal, Runnable onComplete)
    {
        App.getInstance().setImageCompanyLogo(imageLocal);
        onComplete.run();
    }

    public  void setQuestionImage(Context ctx, long questionId, ImageLocal imageLocal, boolean isAltImage, final Runnable onComplete)
    {
        Question question = Questions.getInstance().find(questionId);

        if (question != null) {
            if (isAltImage)
                question.setImageUriTwo(imageLocal.getUri().toString());
            else
                question.setImageUriOne(imageLocal.getUri().toString());

            if (onComplete != null) onComplete.run();
        }
    }
}