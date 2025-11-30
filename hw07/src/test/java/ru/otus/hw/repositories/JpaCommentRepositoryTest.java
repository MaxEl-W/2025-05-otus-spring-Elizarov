package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями")
@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaCommentRepository repository;

    @DisplayName("должен загружать комментприй по id")
    @ParameterizedTest
    @MethodSource("getDbCommentIds")
    void shouldReturnCorrectCommentById(Long commentId) {
        var expectedComment = em.find(Comment.class, commentId);

        var actualComment = repository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent().get().isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список комментариев по книге")
    @Test
    void shouldReturnCorrectCommentsListByBook() {
        var book = em.find(Book.class, 1);
        var expectedComments = IntStream.range(1, 3).boxed().map(bId -> em.find(Comment.class, bId)).toList();

        var actualComments = repository.findByBook(book.getId());

        assertThat(actualComments).containsExactlyElementsOf(expectedComments);
    }

    @DisplayName("должен корректно обновлять комментарий")
    @Test
    void shouldReturnUpdatedComment() {
        var comment = em.find(Comment.class, 1);
        comment.setComment("updated comment");
        em.detach(comment);

        var actualComment = repository.save(comment);

        var expectedComment = em.find(Comment.class, 1);
        assertThat(actualComment).isEqualTo(expectedComment);
    }

    @DisplayName("должен удалять комментарий по его id")
    @Test
    void shouldDeleteCommentById() {
        var comment = em.find(Comment.class, 1);
        assertThat(comment).isNotNull();
        em.detach(comment);

        repository.deleteById(comment.getId());

        var deletedComment = em.find(Comment.class, 1);
        assertThat(deletedComment).isNull();
    }

    private static List<Long> getDbCommentIds() {
        return LongStream.range(1, 3).boxed().toList();
    }
}