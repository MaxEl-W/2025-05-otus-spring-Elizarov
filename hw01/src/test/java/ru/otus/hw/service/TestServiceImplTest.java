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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class TestServiceImplTest {
    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;
    @Mock
    private QuestionConverter converter;
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
    public void shouldConvertAllQuestions() {
        var question = new Question("Question", List.of(new Answer("Answer1", false), new Answer("Answer2", false)));
        given(questionDao.findAll()).willReturn(List.of(question, question, question));

        testService.executeTest();

        verify(converter, times(3)).toString(any(Question.class));
    }
}
