package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами ")
@DataJpaTest
@Import({JpaGenreRepository.class})
class JpaGenreRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaGenreRepository repositoryJpa;

    @DisplayName("должен загружать жанры по идентификаторам")
    @Test
    void shouldReturnCorrectGenreByIds() {
        var ids = LongStream.range(2, 5).boxed().collect(Collectors.toSet());
        var expectedGenres = ids.stream().map(id -> em.find(Genre.class, id)).toList();

        var actualGenres = repositoryJpa.findAllByIds(ids);

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expectedGenres = LongStream.range(1, 7).boxed().map(id -> em.find(Genre.class, id)).toList();

        var actualGenres = repositoryJpa.findAll();

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }
}