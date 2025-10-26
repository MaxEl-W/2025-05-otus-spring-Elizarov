package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final int BEGIN_COMMENT_LINE_COUNT = 1;

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        InputStream questionInputStream = getQuestions();

        CsvToBeanBuilder<QuestionDto> csvToBeanBuilder =
                new CsvToBeanBuilder<>(new InputStreamReader(questionInputStream));
        List<QuestionDto> questions =
                csvToBeanBuilder.withType(QuestionDto.class).withSeparator(';').withSkipLines(BEGIN_COMMENT_LINE_COUNT)
                        .build().parse();

        return questions.stream().map(QuestionDto::toDomainObject).toList();
    }

    private InputStream getQuestions() {
        String questionsFileName = fileNameProvider.getTestFileName();
        try {
            return getFileFromResourceAsStream(questionsFileName);
        } catch (Exception e) {
            throw new QuestionReadException(e.getMessage(), e);
        }
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return Optional.ofNullable(inputStream)
                .orElseThrow(() -> new IllegalArgumentException("file not found! " + fileName));
    }
}
