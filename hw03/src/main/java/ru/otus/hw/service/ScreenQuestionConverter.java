package ru.otus.hw.service;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

@Component
public class ScreenQuestionConverter implements QuestionConverter {
    private static final String OUTPUT_QUESTION_TEMPLATE = "\t%d. %s%n";

    public String toString(Question question) {
        StringBuilder sb = new StringBuilder(question.text());
        sb.append("\n");
        int answerNumber = ANSWER_START_NUMBER;
        for (Answer answer : question.answers()) {
            String formatedQuestion = String.format(OUTPUT_QUESTION_TEMPLATE, answerNumber, answer.text());
            sb.append(formatedQuestion);
            answerNumber++;
        }
        return sb.toString();
    }
}
