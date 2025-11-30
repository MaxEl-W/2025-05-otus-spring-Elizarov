package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface JpaCommentRepository  extends JpaRepository<Comment, Long>, CommentRepository {

    @Override
    Comment save(Comment comment);

    @Query("select c from Comment c where c.book.id = :bookId")
    List<Comment> findByBook(@Param("bookId") long bookId);
}
