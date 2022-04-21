import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class QuestioningTest {
    @Test
    public void singleton_not_null()
    {
        Questioner questioner = Questioner.getInstance();
        assertThat(questioner).isNotNull();
    }

    @Test
    public void range_empty_returns_finished()
    {
        Sections sections = Sections.getInstance();
        Questions questions  = Questions.getInstance();
        Questioner questioner = Questioner.getInstance();

        sections.clear();
        questions.clear();
        questioner.restart(false);

        Questioner.QuestionState state = questioner.moveNext();

        assertThat(state == Questioner.QuestionState.Finished).isTrue();
        assertThat(Questioner.getInstance().getQuestionerState().getCurrentSection()).isNull();
        assertThat(Questioner.getInstance().getQuestionerState().getCurrentQuestion()).isNull();
    }

    @Test
    public void single_section_no_show_cover_move_next_returns_section()
    {
        Sections sections = Sections.getInstance();
        Questions questions  = Questions.getInstance();
        Questioner questioner = Questioner.getInstance();

        sections.clear();
        questions.clear();
        questioner.restart(false);


        Section section = new Section(1, "SectionOne", "SectionOne", false,0, false, true);

        sections.add(section);

        Questioner.QuestionState state = questioner.moveNext();

        assertThat(state == Questioner.QuestionState.Finished).isTrue();

        assertThat(Questioner.getInstance().getQuestionerState().getCurrentSection()).isEqualTo(section);

    }

    @Test
    public void n_section_no_show_cover_move_next_returns_section()
    {
        Sections sections = Sections.getInstance();
        Questions questions  = Questions.getInstance();
        Questioner questioner = Questioner.getInstance();

        sections.clear();
        questions.clear();
        questioner.restart(false);

        for(int i = 1; i <= 3; i++) {
            String sectionName = String.format("Section%d", i);

            Section section = new Section(i, sectionName, sectionName, false, i - 1, false, true);
            sections.add(section);
        }

        Questioner.QuestionState state = questioner.moveNext();

        assertThat(state == Questioner.QuestionState.Finished).isTrue();


    }

    @Test
    public void single_section_show_cover_move_next_returns_section()
    {
        Sections sections = Sections.getInstance();
        Questions questions  = Questions.getInstance();
        Questioner questioner = Questioner.getInstance();

        sections.clear();
        questions.clear();
        questioner.restart(false);

        Section section = new Section(1, "SectionOne", "SectionOne", true,0, false, true);

        sections.add(section);

        Questioner.QuestionState state = questioner.moveNext();

        assertThat(state == Questioner.QuestionState.Info).isTrue();

        assertThat(Questioner.getInstance().getQuestionerState().getCurrentSection()).isEqualTo(section);

    }

    @Test
    public void n_section_show_cover_move_next_returns_section()
    {
        Sections sections = Sections.getInstance();
        Questions questions  = Questions.getInstance();
        Questioner questioner = Questioner.getInstance();

        sections.clear();
        questions.clear();
        questioner.restart(false);

        for(int i = 1; i <= 3; i++) {
            String sectionName = String.format("Section%d", i);

            Section section = new Section(i, sectionName, sectionName, true, i - 1, false, true);
            sections.add(section);
        }

        Questioner.QuestionState state = questioner.moveNext();

        assertThat(state == Questioner.QuestionState.Info).isTrue();
        assertThat(questioner.getQuestionerState().getCurrentSection()).isEqualTo(Sections.getInstance().getAt(0));


        state = questioner.moveNext();
        assertThat(state == Questioner.QuestionState.Info).isTrue();
        assertThat(questioner.getQuestionerState().getCurrentSection()).isEqualTo(Sections.getInstance().getAt(1));

        state = questioner.moveNext();
        assertThat(state == Questioner.QuestionState.Info).isTrue();
        assertThat(questioner.getQuestionerState().getCurrentSection()).isEqualTo(Sections.getInstance().getAt(2));


    }
}
