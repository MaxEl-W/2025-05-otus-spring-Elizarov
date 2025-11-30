package ru.otus.hw.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private BookRepository bookRepository;

    private JpaCommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBook(long bookId) {
        return commentRepository.findByBook(bookId);
    }

    @Override
    @Transactional
    public void addComment(long bookId, String commentText) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(("Book with id %d not found").formatted(bookId)));
        var comment = new Comment(0, book, commentText);
        book.getComments().add(comment);
        bookRepository.save(book);
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
