package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import({CommentServiceImpl.class, CommentConverter.class, JpaCommentRepository.class, JpaBookRepository.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentConverter commentConverter;

    @DisplayName("конвертация комментария в строку не должна вызывать LazyInitializationException")
    @Test
    public void shouldReturnBookWithInitialisedFields() {
        var comment = commentService.findById(1L).get();

        assertThatCode(() -> commentConverter.commentToString(comment)).doesNotThrowAnyException();
    }

    @DisplayName("конвертация комментариев в строку не должна вызывать LazyInitializationException")
    @Test
    public void shouldReturnBooksWithInitialisedFields() {
        var comments = commentService.findByBook(1);

        assertThatCode(() -> comments.forEach(commentConverter::commentToString)).doesNotThrowAnyException();
    }
}
