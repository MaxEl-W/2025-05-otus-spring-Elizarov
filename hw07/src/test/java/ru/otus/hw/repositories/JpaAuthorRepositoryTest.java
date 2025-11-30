package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с авторами ")
@DataJpaTest
@Import({JpaAuthorRepository.class})
class JpaAuthorRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaAuthorRepository repository;

    @DisplayName("должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldReturnCorrectAuthorById(Long authorId) {
        var expectedAuthor = em.find(Author.class, authorId);

        var actualAuthor = repository.findById(authorId);

        assertThat(actualAuthor).isPresent().get().isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var expectedAuthors = getDbAuthors().stream().map(id -> em.find(Author.class, id)).toList();

        var actualAuthors = repository.findAll();

        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }

    private static List<Long> getDbAuthors() {
        return LongStream.range(1, 4).boxed().toList();
    }
}