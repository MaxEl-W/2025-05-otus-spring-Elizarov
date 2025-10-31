package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
public class ScreenQuestionConverterTest {
    @InjectMocks
    private ScreenQuestionConverter converter;

    @Test
    public void shouldValidFormat() {
        var expectedQuestionFormatedText = "Question\n\t1. Answer1\n\t2. Answer2\n";
        var question = new Question("Question", List.of(new Answer("Answer1", false), new Answer("Answer2", false)));

        String actualQuestionFormatedText = converter.toString(question);

        assertThat(actualQuestionFormatedText).isEqualTo(expectedQuestionFormatedText);
    }
}
