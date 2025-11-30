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
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookRepository repository;

    private static List<Long> dbBookIds;

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBookIds")
    void shouldReturnCorrectBookById(Long bookId) {
        Book expectedBook = em.find(Book.class, bookId);

        var actualBook = repository.findById(bookId);

        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> expectedBooks = LongStream.range(1, 4).boxed().map(id -> em.find(Book.class, id)).toList();

        var actualBooks = repository.findAll();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = em.find(Author.class, 1);
        Genre g1 = em.find(Genre.class, 1);
        Genre g2 = em.find(Genre.class, 2);
        var expectedBook = new Book(0, "BookTitle_10500", author, List.of(g1, g2));

        var returnedBook = repository.save(expectedBook);

        expectedBook = em.find(Book.class, returnedBook.getId());

        assertThat(returnedBook).isNotNull().matches(book -> book.getId() > 0).usingRecursiveComparison()
                .ignoringExpectedNullFields().isEqualTo(expectedBook);
        assertThat(em.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = em.find(Book.class, 1L);
        var genre = em.find(Genre.class, 3L);
        expectedBook.getGenres().add(genre);

        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull().matches(book -> book.getId() > 0).usingRecursiveComparison()
                .ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(repository.findById(returnedBook.getId())).isPresent().get().isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(em.find(Book.class, 1L)).isNotNull();

        repository.deleteById(1L);

        assertThat(em.find(Book.class, 1L)).isNull();
    }

    private static List<Long> getDbBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }
}