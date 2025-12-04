package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(long id);

    List<Comment> findByBook(long bookId);

    void addComment(long bookId, String comment);

    void deleteComment(long commentId);
}
