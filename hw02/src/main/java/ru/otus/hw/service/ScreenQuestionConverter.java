package ru.otus.hw.service;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

public class ScreenQuestionConverter implements QuestionConverter {
    private static final String NUMBER_SEPARATOR = ". ";

    public String toString(Question question) {
        StringBuilder sb = new StringBuilder(question.text());
        sb.append("\n");
        int answerNumber = ANSWER_START_NUMBER;
        for (Answer answer : question.answers()) {
            sb.append("\t").append(answerNumber++).append(NUMBER_SEPARATOR).append(answer.text()).append("\n");
        }
        return sb.toString();
    }
}
