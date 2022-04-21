package com.mb.prestartcheck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Size;

import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class QuestionOptions  extends Question {
    public String option1;
    public String option2;
    private String optionNone;
    public ImageLocal imageOptionOne;
    public ImageLocal imageOptionTwo;

    public String getOption1() { return this.option1;}
    public String getOption2() { return this.option2;}
    public String getOptionNone() { return this.optionNone;}

    public  ImageLocal getImageOptionOne() { return this.imageOptionOne;}
    public  ImageLocal getImageOptionTwo() { return this.imageOptionTwo;}


    @Override
    protected void refreshChild(TupleQuestionSection row) {

        if(this.imageUriOne == null || ( this.imageUriOne != null && imageUriOne.isEmpty()) ) this.imageOptionOne = null;
        if(this.imageUriTwo == null || ( this.imageUriTwo != null && imageUriTwo.isEmpty()) ) this.imageOptionTwo = null;

        this.option1 = row.custom_field_2;
        this.option2 = row.custom_field_3;
        this.optionNone = row.custom_field_4;

    }

    @Override
    public String getTypeString() {
        return "Option";
    }

    @Override
    public boolean isExpectedResponse(Response response) {
        if (this.expectedAnswer == null ) return true;

        //Take "No expected answer" from  resources
        if (this.expectedAnswer.isEmpty())  return true;

        return this.expectedAnswer.compareToIgnoreCase(response.getOperatorResponse()) == 0;

    }

    public static void loadResources(QuestionOptions instance, Context context)
    {

        if (instance.imageUriOne != null && !instance.imageUriOne.isEmpty() )
        {
            Uri uri= Uri.parse(instance.imageUriOne);
            if (instance.imageOptionOne == null) instance.imageOptionOne = new ImageLocal(uri, null);
            try {

                if (instance.imageOptionOne.getDisplayImage() == null)
                {
                            InputStream is = context.getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            instance.imageOptionOne.setDisplayImage(bitmap);

                }

                if (instance.imageOptionOne.getThumbNail() == null)
                {
                    // Load thumbnail of a specific media item.
                    Bitmap thumbnail =  context.getContentResolver().loadThumbnail(uri, new Size(640, 480), null);

                    instance.imageOptionOne.setThumbNail(thumbnail);
                }

            }
            catch(FileNotFoundException fex)
            {
                AppLog.getInstance().print("Could not find the image associated with question %s. %s",
                        instance.title, fex.getMessage());
            }
            catch(IOException ioex)
            {
                AppLog.getInstance().print("Could not find the thumbnail image associated with question %s. %s",
                        instance.title, ioex.getMessage());

            }
            catch (Exception ex)
            {
                AppLog.getInstance().print(ex.getMessage());

            }


        }

        if (instance.imageUriTwo != null && !instance.imageUriTwo.isEmpty() )
        {
            Uri uri= Uri.parse(instance.imageUriTwo);
            if (instance.imageOptionTwo == null) instance.imageOptionTwo = new ImageLocal(uri, null);
            try {

                if (instance.imageOptionTwo.getDisplayImage() == null)
                {
                    InputStream is = context.getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    instance.imageOptionTwo.setDisplayImage(bitmap);
                }
                if (instance.imageOptionTwo.getThumbNail() == null)
                {
                    // Load thumbnail of a specific media item.
                    Bitmap thumbnail =  context.getContentResolver().loadThumbnail(uri, new Size(640, 480), null);

                    instance.imageOptionTwo.setThumbNail(thumbnail);
                }
            }

            catch(FileNotFoundException fex)
            {
                AppLog.getInstance().print("Could not find the image associated with question %s. %s",
                        instance.title, fex.getMessage());
            }
            catch(IOException ioex)
            {
                AppLog.getInstance().print("Could not find the thumbnail image associated with question %s. %s",
                        instance.title, ioex.getMessage());

            }
            catch (Exception ex)
            {
                AppLog.getInstance().print(ex.getMessage());
            }

        }


    }

    public boolean hasOptionOneImage() { return this.imageOptionOne != null && this.imageOptionOne.getThumbNail() != null
     && this.imageOptionOne.getDisplayImage() != null; }

    public boolean hasOptionTwoImage() { return this.imageOptionTwo != null && this.imageOptionTwo.getThumbNail() != null
            && this.imageOptionTwo.getDisplayImage() != null; }

    public void removeResources()
    {
        imageOptionOne = null;
        imageOptionTwo = null;
    }
    public void setImageOptionOne(ImageLocal imageQuestion) {
        this.imageOptionOne = imageQuestion;
    }
    public void setImageOptionTwo(ImageLocal imageQuestion) {
        this.imageOptionTwo = imageQuestion;
    }


}
