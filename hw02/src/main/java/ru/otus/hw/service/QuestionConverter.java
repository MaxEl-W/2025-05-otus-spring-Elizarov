package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

public interface QuestionConverter {
    int ANSWER_START_NUMBER = 1;

    String toString(Question question);
}
