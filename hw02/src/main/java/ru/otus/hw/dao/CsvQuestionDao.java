package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final int BEGIN_COMMENT_LINE_COUNT = 1;

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        CsvToBeanBuilder<QuestionDto> csvToBeanBuilder;
        List<QuestionDto> questions;
        try (InputStream inputStream = getFileFromResourceAsStream();
             InputStreamReader questionReader = new InputStreamReader(inputStream);) {
            csvToBeanBuilder = new CsvToBeanBuilder<>(questionReader);
            questions = csvToBeanBuilder.withType(QuestionDto.class).withSeparator(';')
                    .withSkipLines(BEGIN_COMMENT_LINE_COUNT).build().parse();
        } catch (Exception e) {
            throw new QuestionReadException(e.getMessage(), e);
        }

        return questions.stream().map(QuestionDto::toDomainObject).toList();
    }

    private InputStream getFileFromResourceAsStream() {
        String fileName = fileNameProvider.getTestFileName();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return Optional.ofNullable(inputStream)
                .orElseThrow(() -> new IllegalArgumentException("file not found! " + fileName));
    }
}
