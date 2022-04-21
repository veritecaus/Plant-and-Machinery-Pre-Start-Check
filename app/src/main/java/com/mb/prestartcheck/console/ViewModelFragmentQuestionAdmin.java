package com.mb.prestartcheck.console;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mb.prestartcheck.App;
import com.mb.prestartcheck.FactoryQuestion;
import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.data.DaoQuestion;
import com.mb.prestartcheck.data.DaoSectionQuestion;
import com.mb.prestartcheck.data.PrestartCheckDatabase;
import com.mb.prestartcheck.data.TableQuestion;
import com.mb.prestartcheck.data.TableSectionQuestion;

import java.util.Date;


public class ViewModelFragmentQuestionAdmin extends AndroidViewModel {
    protected Question question;
    protected long sectionId;
    protected boolean isNew;



    public ViewModelFragmentQuestionAdmin(@NonNull  Application application, long quid, int type, long sid, boolean isNew) {
        super(application);
        this.question = isNew ? FactoryQuestion.create(type) : Questions.getInstance().find(quid);
        this.sectionId = sid;
        this.isNew = isNew;
    }

    public  Question getQuestion() { return this.question;}
    public  long getSectionId() { return sectionId;}

    public int getNextQuestionSequence()
    {
        return Questions.getInstance().getNextSquence(sectionId);
    }

    public void updateQuestion(String title, int type, int seq,  boolean posneg, String altText, boolean isActive,
                               boolean isCritical, boolean enableMachine, int timeout, String expectedAnswer,
                               String expectedAnswerNeg, String cusField1, String cusField2, String cusField3,
                               String cusField4, final String comment, Runnable onUpdated)
    {

        try {
            TableQuestion tableQuestion = new TableQuestion();
            tableQuestion.id = this.question.getId();
            tableQuestion.title = title;
            tableQuestion.type_id = type;
            tableQuestion.number = question.getNumber();
            tableQuestion.sequence = seq;
            tableQuestion.is_negativepostive = posneg ? 1 : 0;
            tableQuestion.alternative_title = altText;
            tableQuestion.enabled = isActive ? 1 : 0;
            tableQuestion.is_critical = isCritical ? 1 : 0;
            tableQuestion.allow_machine_op = enableMachine ? 1 : 0;
            tableQuestion.time_out = timeout;
            tableQuestion.deleted = 0;

            Date now = new Date();
            tableQuestion.updated_datetime = now;
            tableQuestion.created_datetime = this.question.getCreateDateTime();
            tableQuestion.custom_field_1 = cusField1;
            tableQuestion.custom_field_2 = cusField2;
            tableQuestion.custom_field_3 = cusField3;
            tableQuestion.custom_field_4 = cusField4;
            tableQuestion.expected_answer = expectedAnswer;
            tableQuestion.expected_answer_neg = expectedAnswerNeg;
            tableQuestion.image_uri_one = this.question.getImageUriOne();
            tableQuestion.image_uri_two  = this.question.getImageUriTwo();
            tableQuestion.image_uri_three  = this.question.getImageUriThree();
            tableQuestion.image_uri_four  = this.question.getImageUriFour();
            tableQuestion.comment = comment;

            DaoQuestion daoQuestion = PrestartCheckDatabase.getDatabase(this.getApplication()).getDaoQuestion();

            PrestartCheckDatabase.getDatabaseWriteExecutor().execute(new Runnable() {
                @Override
                public void run() {

                    if (ViewModelFragmentQuestionAdmin.this.isNew)
                    {
                        long newId = daoQuestion.insert(tableQuestion);

                        DaoSectionQuestion daoSectionQuestion = PrestartCheckDatabase.getDatabase(ViewModelFragmentQuestionAdmin.this.getApplication()).getDaoSectionQuestion();
                        TableSectionQuestion sectionQuestion = new TableSectionQuestion();
                        sectionQuestion.id = 0;
                        sectionQuestion.deleted = 0;
                        sectionQuestion.enabled = 1;
                        sectionQuestion.question_id =newId;
                        sectionQuestion.section_id = ViewModelFragmentQuestionAdmin.this.sectionId;
                        Date now = new Date();
                        sectionQuestion.created_datetime = now.getTime();
                        sectionQuestion.updated_datetime = now.getTime();

                        daoSectionQuestion.insert(sectionQuestion);

                    }
                    else
                        daoQuestion.update(tableQuestion);


                    //Wait for questions to synchronize from the database.
                    try {Thread.sleep(1000);} catch(InterruptedException iex){}

                    onUpdated.run();
                }
            });

        }
        catch(Exception ex)
        {
            Log.e(App.TAG, ex.getMessage());
        }


    }

    public void clearImage(Context context, boolean isAltImage, final Runnable onComplete)
    {
        PrestartCheckDatabase prestartCheckDatabase = PrestartCheckDatabase.getDatabase(context);
        DaoQuestion daoQuestion = prestartCheckDatabase.getDaoQuestion();

        prestartCheckDatabase.getQueryExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final Date now = new Date();
                if (!isAltImage)
                    daoQuestion.updateImageURIOne(question.getId(), null, now.getTime());
                else
                    daoQuestion.updateImageURITwo(question.getId(), null, now.getTime());

                //Wait for the database to update
                try {Thread.sleep(1000);} catch (InterruptedException iex){}

                if (onComplete!= null) onComplete.run();
            }
        });

    }

}
