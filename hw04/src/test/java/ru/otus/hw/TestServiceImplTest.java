package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
@SpringBootTest(classes = {TestServiceImpl.class})
public class TestServiceImplTest {
    @MockitoBean
    private LocalizedIOService ioService;
    @MockitoBean
    private QuestionDao questionDao;
    @MockitoBean
    private QuestionConverter converter;
    @MockitoBean
    private AnswerReader answerReader;
    @Autowired
    private TestServiceImpl testService;

    private final Student student = new Student("Ivan", "Petrov");

    @Test
    public void shouldPrintGrating() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());
        String expectedGrating = "TestService.answer.the.questions";

        testService.executeTestFor(student);

        verify(ioService, times(2)).printLine("");
        verify(ioService, times(1)).printLineLocalized(expectedGrating);
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
