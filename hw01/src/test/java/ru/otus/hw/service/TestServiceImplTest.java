package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class TestServiceImplTest {
    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;
    @InjectMocks
    private TestServiceImpl testService;

    @Test
    public void shouldPrintGrating() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());
        String expectedGrating = "Please answer the questions below%n";

        testService.executeTest();

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine(expectedGrating);
    }

    @Test
    public void shouldValidFormat() {
        var expectedQuestionFormatedText = "Question\n\tAnswer1\n\tAnswer2\n";
        var question = new Question("Question", List.of(new Answer("Answer1", false), new Answer("Answer2", false)));
        given(questionDao.findAll()).willReturn(List.of(question));
        var captor = ArgumentCaptor.forClass(String.class);

        testService.executeTest();

        verify(ioService, times(2)).printLine(captor.capture());
        var actualQuestionFormatedText = captor.getAllValues().get(1);
        assertThat(actualQuestionFormatedText).isEqualTo(expectedQuestionFormatedText);
    }
}
