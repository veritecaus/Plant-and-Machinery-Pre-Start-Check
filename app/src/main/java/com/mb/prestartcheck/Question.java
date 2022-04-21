package com.mb.prestartcheck;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.DaoSectionQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TupleQuestionSection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Question {

    /**
     *  Defines.
     *
     */
    public static final int MAX_MACHINE_OP_COUNT = 3;

    /**
     * Common question properties.
     * Plase don't put specifics here.
     */
    protected long id = 0;
    protected String title ="";
    protected int number = 0;
    protected int sequence = 0;
    //Operate the machine during this question.
    protected boolean allowMachineOperation = false;

    //Amount of time to operate the machine before
    //automatically shutting down ( in seconds).
    protected int timeOut =0;
    protected boolean enabled = true;
    protected boolean isCritical = false;
    protected boolean isNegativePositive = false;
    protected String titleAlternative = "";
    protected String comment; // linked to the custom field 4 in the database.
    protected String altComment; // linked to the custom field 4 in the database.
    protected Date createDateTime = new Date();
    protected Date updatedDateTime = new Date();
    protected long sectionId = -1;
    //Questioning order.
    protected int questioningOrder;
    protected String expectedAnswer = "";
    protected String expectedAnswerNeg = "";

    //Images
    protected String imageUriOne = "";
    protected String imageUriTwo = "";
    protected String imageUriThree = "";
    protected String imageUriFour = "";
    public ImageLocal imageQuestion;

    //When this question was asked , how many times
    //did the machine operate. If timeOut seconds has ellapsed, then
    //this increment this value. This is a non persistent value.
    protected int opCounter;

    public static final Predicate<Question> pred_question_enabled = new Predicate<Question>() {
        @Override
        public boolean test(Question question) {
            return question == null ? false : question.getEnabled();

        }
    };

    public static final BiPredicate<Question, Integer> pred_question_next_enabled_sequence = new BiPredicate<Question, Integer>() {
        @Override
        public boolean test(Question question, Integer sequence) {
            if (question == null) return false;
            return question.getEnabled() &&  question.getQuestioningOrder() <sequence;

        }
    };

    public static final BiPredicate<Question, Integer> pred_question_enabled_sequence = new BiPredicate<Question, Integer>() {
        @Override
        public boolean test(Question question, Integer sequence) {
            if (question == null) return false;
            return question.getEnabled() &&  question.getQuestioningOrder() == sequence;

        }
    };

    //Only a supervisor can override this flag.
    protected Section parent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getTitleAlternative() {
        return this.titleAlternative;
    }

    public void setTitleAlternative(String value) {
        this.titleAlternative = value;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int value) {
        this.number = value;
    }

    public int getTimeout() {
        return this.timeOut;
    }

    public void setTimeout(int value) {
        this.timeOut = value;
    }

    public boolean getAllowMachineOperation() {
        return this.allowMachineOperation;
    }

    public void setAllowMachineOperation(boolean value) {
        this.allowMachineOperation = value;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public boolean getIsCritical() {
        return this.isCritical;
    }

    public void setIsCritical(boolean value) {
        this.isCritical = value;
    }

    public boolean getIsNegativePositive() {
        return this.isNegativePositive;
    }

    public void setIsNegativePositive(boolean value) {
        this.isNegativePositive = value;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public Date getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setCreateDateTime(Date e) {
        createDateTime = e;
    }

    public void setUpdatedDateTime(Date e) {
        updatedDateTime = e;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long e) {
        this.sectionId = e;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int s) {
        this.sequence = s;
    }

    public int getQuestioningOrder() {
        return this.questioningOrder;
    }

    public void setQuestioningOrder(int val) {
        this.questioningOrder = val;
    }

    public String getExpectedAnswer() { return this.expectedAnswer ;}
    public void setExpectedAnswer(String e) { this.expectedAnswer = e;}

    public String getExpectedAnswerNeg() { return this.expectedAnswerNeg ;}
    public void setExpectedAnswerNeg(String e) { this.expectedAnswerNeg = e;}

    public  void setParent (Section section) { this.parent = section;}
    public  Section getParent(){ return this.parent;}

    public String getImageUriOne() { return this.imageUriOne ;}
    public void setImageUriOne(String e) { this.imageUriOne = e;}

    public String getImageUriTwo() { return this.imageUriTwo ;}
    public void setImageUriTwo(String e) { this.imageUriTwo = e;}

    public String getImageUriThree() { return this.imageUriThree ;}
    public void setImageUriThree(String e) { this.imageUriThree = e;}

    public String getImageUriFour() { return this.imageUriFour ;}
    public void setImageUriFour(String e) { this.imageUriFour = e;}

    public ImageLocal getImageQuestion() { return this.imageQuestion;}

    @NonNull
    public final String getAltComment() { return altComment;}
    public void setAltComment(@NonNull final String pComment ) { altComment = pComment;}

    @NonNull
    public final String getComment() { return comment;}
    public void setComment(@NonNull final String pComment ) { comment = pComment;}

    // Getter and setter for opCounter.
    public int getOpCounter() { return opCounter;}
    public void setOpCounter(int val) { opCounter = val;}

    protected void refresh(TupleQuestionSection row) {

        this.id = row.id;
        this.title = row.title;
        this.number = row.number;
        this.allowMachineOperation = row.allow_machine_op > 0;
        this.timeOut = row.time_out;
        this.enabled = row.enabled > 0;
        this.isCritical = row.is_critical > 0;
        this.isNegativePositive = row.is_negativepostive > 0;
        this.titleAlternative = row.alternative_title;
        this.updatedDateTime = new Date(row.updated_datetime);
        this.createDateTime = new Date(row.created_datetime);
        this.sectionId = row.section_id;
        this.sequence = row.sequence;
        this.expectedAnswer = row.expected_answer;
        this.expectedAnswerNeg = row.expected_answer_neg;
        this.imageUriOne = row.image_uri_one;
        this.imageUriTwo = row.image_uri_two;
        this.imageUriThree = row.image_uri_three;
        this.imageUriFour  = row.image_uri_four;
        this.comment = row.comment;

        refreshChild(row);

    }

    protected abstract void refreshChild(TupleQuestionSection row);

    @NonNull
    @Override
    public String toString() {
        return String.format("%d - %s", this.sequence, this.title);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Question)) return false;

        return ((Question) obj).getId() == id;
    }

    public static void delete(Application app, long id, Runnable ondeleted) {
        DaoQuestion daoQuestion = PrestartCheckDatabase.getDatabase(app).getDaoQuestion();
        DaoSectionQuestion daoSectionQuestion = PrestartCheckDatabase.getDatabase(app).getDaoSectionQuestion();

        PrestartCheckDatabase.getDatabaseWriteExecutor().execute(new Runnable() {
            @Override
            public void run() {
                daoQuestion.delete(id, (new Date()).getTime());
                daoSectionQuestion.deleteQuestion(id, (new Date()).getTime());
                ondeleted.run();
            }
        });
    }

    public static Boolean isQuestionRequireResponse(Question question)
    {
        //All questions except the section cover ? ( probably the name is wrong).
        return question != null && !QuestionCoverPage.class.isAssignableFrom(question.getClass());
    }

    public abstract String getTypeString();

    public  abstract boolean isExpectedResponse(Response response);

    public  static void loadResources(@NonNull Question instance, Context context)
    {
        if (instance.imageUriOne != null && !instance.imageUriOne.isEmpty() )
        {
            Uri uri= Uri.parse(instance.imageUriOne);

            if (instance.imageQuestion == null) instance.imageQuestion = new ImageLocal(uri, null);

            try {
                InputStream is = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap= BitmapFactory.decodeStream(is);

                instance.imageQuestion.setDisplayImage(bitmap);

                if (instance.imageQuestion.getThumbNail() == null)
                {
                    // Load thumbnail of a specific media item.
                    Bitmap thumbnail =  context.getContentResolver().loadThumbnail(uri, new Size(640, 480), null);

                    instance.imageQuestion.setThumbNail(thumbnail);
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
            catch(Exception ex)
            {
                AppLog.getInstance().print(ex.getMessage());

            }
        }
    }

    public static boolean hasImage(@NonNull Question instance)
    {
        return instance.imageUriOne != null && !instance.imageUriOne.isEmpty() && instance.imageQuestion != null
                &&  instance.imageQuestion.getThumbNail() != null && instance.imageQuestion.getDisplayImage() != null;
    }

    public abstract void removeResources();

}
