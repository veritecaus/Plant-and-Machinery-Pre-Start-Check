package com.mb.prestartcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mb.prestartcheck.data.TableSection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Section {

    private ArrayList<Question> questions = new ArrayList<>();
    private long id;
    private String title;
    private String description;
    private boolean showCoverPage;
    private int sequence;
    private boolean randomQuestions;
    private boolean enabled;
    private Date createdDateTime;
    private Date updatedDatetime;
    private int displaySequence;


    private static Section NewItem = new Section(-1,"New Section", "", false, -1, false, true);

    public static final Predicate<Section> pred_section_enabled = new Predicate<Section>() {
        @Override
        public boolean test(Section section) {
            return section == null ? false : section.getEnabled();

        }
    };

    public static final BiPredicate<Section, Integer> pred_section_sequence = new BiPredicate<Section, Integer>() {
        @Override
        public boolean test(Section section, Integer sequence) {
            return section.getSequence() == sequence;

        }
    };

    public static final BiPredicate<Section, Integer> pred_section_display_sequence = new BiPredicate<Section, Integer>() {
        @Override
        public boolean test(Section section, Integer sequence) {
            return section.getDisplaySequence() == sequence;

        }
    };

    public long getId() { return this.id;}
    public void setId(long e) { this.id = e;}

    public String getTitle() { return this.title;}
    public void setTitle(String e) { this.title = e;}

    public String getDescription() { return this.description;}
    public void setDescription(String e) { this.description = e;}

    public boolean getShowCoverPage() { return this.showCoverPage;}
    public  void setShowCoverPage(Boolean e) { this.showCoverPage = e;}

    public  int getSequence() { return this.sequence;}
    public  void setSequence(int e) { this.sequence = e;}

    public  int getDisplaySequence() { return this.displaySequence;}
    public  void setDisplaySequence(int e) { this.displaySequence = e;}

    public  boolean getRandomQuestions() { return this.randomQuestions;}
    public  void setRandomQuestions(boolean e) { this.randomQuestions = e;}

    public boolean getEnabled() { return this.enabled;}
    public void setEnabled(boolean  e) { this.enabled = e;}

    public  Date getCreatedDateTime() { return this.createdDateTime;}
    public  void setCreatedDatetime(Date e) {  this.createdDateTime  = e ;}

    public  Date getUpdatedDatetime() { return this.updatedDatetime;}
    public  void setUpdatedDatetime(Date e) {  this.updatedDatetime = e;}

    public  static Section getNewItem() { return NewItem;}

    public  Section(long id, String t, String desc, boolean showcover, int seq, boolean rQuest, boolean enabled)
    {
        this.id = id;
        this.title = t;
        this.description = desc;
        this.showCoverPage = showcover;
        this.sequence = seq;
        this.randomQuestions = rQuest;
        this.enabled = enabled;
    }

    public static  Section createFromTableRow(TableSection row)
    {
        Section s = new  Section(row.id, row.title, row.description, row.show_cover_page > 0 , row.sequence,
                row.random_questions > 0, row.enabled > 0);

        s.createdDateTime = new Date(row.created_datetime);
        s.updatedDatetime = new Date(row.updated_datetime);

        return s;
    }

    public static boolean addQuestion(Section s, Question q)
    {
        if (!s.questions.contains(q)) {
            s.questions.add(q);
            q.setParent(s);
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof  Section) ) return false;

        return ((Section)obj).getId() == id;

    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }

    public static void sync(Section s, TableSection src)
    {
        s.title = src.title;
        s.description = src.description;
        s.showCoverPage = src.show_cover_page > 0;
        s.sequence = src.sequence;
        s.enabled = src.enabled > 0;
        s.randomQuestions = src.random_questions > 0;
        s.createdDateTime = new Date(src.created_datetime);
        s.updatedDatetime = new Date(src.updated_datetime);
    }

    public  int getSize()
    {
        return questions.size();
    }

    public  int indexOf(Question question)
    {
        return this.questions.indexOf(question);
    }

    public  int count(Predicate<Question> predicate)
    {
        int cnt = 0;
        for(Question question : this.questions)
            cnt += predicate.test(question) ?  1 : 0;

        return cnt;
    }

    public Question getFirst()
    {
        return questions.size() > 0 ? questions.get(0) : null;
    }

    public Question getLast()
    {
        return questions.size() > 0 ? questions.get(questions.size() -1 ) : null;
    }

    public Question getAt(int idx)
    {
        return questions.size() > 0 ? questions.get(idx) : null;
    }

    public static Question findBySequence(Section s , int sequence)
    {
        for(Question q : s.questions)
        {
            if (q.getQuestioningOrder() == sequence)
                return q;
        }

        return null;
    }

    public List<Question> find(Predicate<Question> predicate)
    {
        ArrayList<Question> tmp = new ArrayList<Question>();
        for(Question question : this.questions)
            if (predicate.test(question)) tmp.add(question);
        return tmp;
    }


    @Nullable
    public Question find(BiPredicate<Question, Integer> predicate, Integer questioningOrder)
    {
        for(Question question : this.questions)
            if (predicate.test(question , questioningOrder)) return question;

        return null;
    }



    public  static TableSection toTable(Section s, boolean deleted)
    {
        TableSection t = new TableSection();
        t.id = s.id;
        t.title = s.title;
        t.sequence = s.sequence;
        t.description = s.description;
        t.show_cover_page = s.showCoverPage ? 1 : 0;
        t.random_questions = s.randomQuestions ? 1 :0;
        t.enabled = s.enabled ? 1: 0;
        Date now = new Date();
        t.updated_datetime = now.getTime();
        t.created_datetime = s.createdDateTime.getTime();
        t.deleted = deleted ? 1 : 0;

        return t;

    }

    public static int getMaxSequence(Section s)
    {
        int max = -1;
        for(int i = 0; i < s.getSize(); i++)
        {
            max  = max > s.getAt(i).getQuestioningOrder() ? max : s.getAt(i).getQuestioningOrder();
        }

        return max;
    }

    public static int getMaxSequence(Section s, Predicate<Question> predicate)
    {
        int max = -1;
        for(int i = 0; i < s.getSize(); i++)
        {
            if (predicate.test(s.getAt(i)))
                max  = max > s.getAt(i).getQuestioningOrder() ? max : s.getAt(i).getQuestioningOrder();
        }

        return max;
    }

    public  static void sortQuestions(Section s, Comparator<Question> sorter)
    {
        Collections.sort(s.questions, sorter);
    }


    public void setup()
    {
        List<Question> enabled = this.find(Question.pred_question_enabled);

        if (enabled.size() == 0) return;

        if (this.randomQuestions)
        {

            ArrayList<Integer> nr = Lottery.getInstance().getRandomSet(enabled.size());

            for(int pos = 1; pos <= enabled.size(); pos++)
                enabled.get(nr.get(pos-1)).setQuestioningOrder(pos);

            //Collections.sort(this.questions, new ComparerQuestioningOrder());
        }
        else
        {
            //Sort questions by sequence field first before
            //assigning order of display.


            Collections.sort(enabled, new ComparerQuestionSequence());

            for (int i = 1; i <= enabled.size(); i++)
                enabled.get(i - 1).setQuestioningOrder(i);

            //Collections.sort(this.questions, new ComparerQuestionSequence());
        }
    }

    public  void remove(Question question)
    {
        this.questions.remove(question);
    }


}
