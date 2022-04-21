import com.mb.prestartcheck.Question;
import com.mb.prestartcheck.Questioner;
import com.mb.prestartcheck.Questions;
import com.mb.prestartcheck.Section;
import com.mb.prestartcheck.Sections;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;


public class QuestionnairTest {

    @Test
    public void construction_returns_not_null()
    {
        Questioner questioner = new Questioner();
        assertThat(questioner).isNotNull();

    }




}
