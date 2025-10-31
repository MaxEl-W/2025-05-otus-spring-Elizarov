package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import static ru.otus.hw.service.QuestionConverter.ANSWER_START_NUMBER;

@Service
@RequiredArgsConstructor
public class StreamsIOAnswerReaderImpl implements AnswerReader {
    private final IOService ioService;

    public Answer readUserChoice(Question question) {
        int answerCount = question.answers().size();
        int max = answerCount + ANSWER_START_NUMBER - 1;

        try {
            int userChoice = ioService.readIntForRangeWithPrompt(ANSWER_START_NUMBER, max, "Choose right answer:",
                    "The answer was entered incorrectly");
            int index = userChoice - ANSWER_START_NUMBER;
            return question.answers().get(index);
        } catch (IllegalArgumentException e) {
            ioService.printLine("The number of attempts has been exhausted. The answer is not counted.\n");
            return null;
        }
    }
}
