package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({BookServiceImpl.class, BookConverter.class, AuthorConverter.class, GenreConverter.class,
        CommentConverter.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookConverter bookConverter;

    @DisplayName("должен получать книгу по егё id, с инициализированными Lazy полями")
    @Test
    public void shouldReturnBookWithInitialisedFields() {
        var book = bookService.findById(1L).get();

        assertThatCode(() -> bookConverter.bookToString(book)).doesNotThrowAnyException();
    }

    @DisplayName("должен получать все книги, с инициализированными Lazy полями")
    @Test
    public void shouldReturnBooksWithInitialisedFields() {
        var books = bookService.findAll();

        assertThatCode(() -> books.forEach(bookConverter::bookToString)).doesNotThrowAnyException();
    }
}
