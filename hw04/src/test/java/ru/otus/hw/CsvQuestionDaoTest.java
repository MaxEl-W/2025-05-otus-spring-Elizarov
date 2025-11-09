package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
public class CsvQuestionDaoTest {
    @Mock
    private TestFileNameProvider fileNameProvider;
    @InjectMocks
    private CsvQuestionDao questionDao;

    @Test
    public void shouldThrowExceptionWhenFileNotFound() {
        String wrongPath = "wrongPath";
        given(fileNameProvider.getTestFileName()).willReturn(wrongPath);

        QuestionReadException exception = assertThrows(QuestionReadException.class, questionDao::findAll);

        assertEquals("file not found! " + wrongPath, exception.getMessage());
    }

    @Test
    public void shouldRead() {
        given(fileNameProvider.getTestFileName()).willReturn("testquestions.csv");

        var questions = questionDao.findAll();

        assertEquals(2, questions.size());
        assertEquals(2, questions.get(0).answers().size());

        int indexRightAnswer = getIndexRightAnswer(questions.get(1));
        assertEquals(0, indexRightAnswer);
    }

    private int getIndexRightAnswer(Question question) {
        int index = 0;
        for (var answer: question.answers()) {
            if (answer.isCorrect()) {
                return index;
            }
            index++;
        }
        throw new RuntimeException("Incorrect test file");
    }
}
