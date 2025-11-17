package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.ScreenQuestionConverter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
@SpringBootTest(classes = {ScreenQuestionConverter.class})
public class ScreenQuestionConverterTest {
    @Autowired
    private ScreenQuestionConverter converter;

    @Test
    public void shouldValidFormat() {
        var expectedQuestionFormatedText = "Question\n" + String.format("\t1. %s%n\t2. %s%n", "Answer1", "Answer2");
        var question = new Question("Question", List.of(new Answer("Answer1", false), new Answer("Answer2", false)));

        String actualQuestionFormatedText = converter.toString(question);

        assertThat(actualQuestionFormatedText).isEqualTo(expectedQuestionFormatedText);
    }
}
