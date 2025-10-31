package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static ru.otus.hw.service.QuestionConverter.ANSWER_START_NUMBER;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final int DEFAULT_WRONG_ANSWER = -1;

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final QuestionConverter converter;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            String questionText = converter.toString(question);
            ioService.printLine(questionText);

            var userChoiceAnswerArrayIndex = readUserChoice(question);
            var isAnswerValid = isRightAnswerNumber(question, userChoiceAnswerArrayIndex);

            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private int readUserChoice(Question question) {
        int answerCount = question.answers().size();
        int max = answerCount + ANSWER_START_NUMBER - 1;

        try {
            int userChoice = ioService.readIntForRangeWithPrompt(ANSWER_START_NUMBER, max, "Choose right answer:",
                    "The answer was entered incorrectly");
            return userChoice - ANSWER_START_NUMBER;
        } catch (IllegalArgumentException e) {
            ioService.printLine("The number of attempts has been exhausted. The answer is not counted.\n");
            return DEFAULT_WRONG_ANSWER;
        }
    }

    private boolean isRightAnswerNumber (Question question, int userChoiceAnswerArrayIndex) {
        if (userChoiceAnswerArrayIndex == DEFAULT_WRONG_ANSWER) {
            return false;
        }

        return question.answers().get(userChoiceAnswerArrayIndex).isCorrect();
    }
}
