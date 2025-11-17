package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import static ru.otus.hw.service.QuestionConverter.ANSWER_START_NUMBER;

@Service
@RequiredArgsConstructor
public class StreamsIOAnswerReaderImpl implements AnswerReader {
    private final LocalizedIOService ioService;

    public Answer readUserChoice(Question question) {
        int maxAnswerNumber = question.answers().size() + ANSWER_START_NUMBER - 1;

        try {
            int userChoice = ioService.readIntForRangeWithPromptLocalized(ANSWER_START_NUMBER, maxAnswerNumber,
                    "AnswerReader.input.right.answer", "AnswerReader.wrong.answer");
            int index = userChoice - ANSWER_START_NUMBER;
            return question.answers().get(index);
        } catch (IllegalArgumentException e) {
            ioService.printLineLocalized("AnswerReader.attempts.exhausted");
            return null;
        }
    }
}
