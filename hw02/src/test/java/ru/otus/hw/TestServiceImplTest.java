package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private AnswerReader answerReader;
    @InjectMocks
    private TestServiceImpl testService;

    private final Student student = new Student("Ivan", "Petrov");

    @Test
    public void shouldPrintGrating() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());
        String expectedGrating = "Please answer the questions below%n";

        testService.executeTestFor(student);

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine(expectedGrating);
    }

    @Test
    public void shouldConvertAllQuestions() {
        var question = new Question("Question", List.of(new Answer("Answer1", true), new Answer("Answer2", false)));
        var anotherQuestion = new Question("anotherQuestion", List.of(new Answer("Answer1", true), new Answer("Answer2",
                false)));
        given(questionDao.findAll()).willReturn(List.of(question, question, anotherQuestion));

        given(answerReader.readUserChoice(question)).willReturn(question.answers().get(1));
        given(answerReader.readUserChoice(anotherQuestion)).willReturn(question.answers().get(0));

        TestResult result = testService.executeTestFor(student);

        verify(converter, times(3)).toString(any(Question.class));

        assertEquals(1, result.getRightAnswersCount());
    }
}
