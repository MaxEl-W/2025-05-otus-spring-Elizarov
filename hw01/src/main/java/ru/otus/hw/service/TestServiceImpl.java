package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        List<Question> questions = questionDao.findAll();
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        QuestionScreenConverter converter = new QuestionScreenConverter();
        for (Question question : questions) {
            String questionText = converter.toString(question);
            ioService.printLine(questionText);
        }
    }

    public static class QuestionScreenConverter {
        public String toString(Question question) {
            StringBuilder sb = new StringBuilder(question.text());
            sb.append("\n");
            for (Answer answer : question.answers()) {
                sb.append("\t");
                sb.append(answer.text());
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
