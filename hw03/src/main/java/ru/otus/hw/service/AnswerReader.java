package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

public interface AnswerReader {
    Answer readUserChoice(Question question);
}
