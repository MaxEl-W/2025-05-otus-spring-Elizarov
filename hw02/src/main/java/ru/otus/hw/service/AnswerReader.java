package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

public interface AnswerReader {
    int DEFAULT_WRONG_ANSWER = -1;

    Answer readUserChoice(Question question);
}
