package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.SpecHints;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "repository-type", havingValue = "jpa")
public class JpaBookRepository implements BookRepository {
    private static final String NO_ENTRIES_UPDATED_MESSAGE = "Ни одной записи не обновлено";

    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        Book result = em.find(Book.class, id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");
        var query = em.createQuery("select b from Book b", Book.class);
        query.setHint(SpecHints.HINT_SPEC_FETCH_GRAPH, entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var book = findById(id).orElseThrow(
                () -> new EntityNotFoundException(("Book with id %d not found").formatted(id)));
        em.remove(book);
    }

    private Book insert(Book book) {
        em.persist(book);
        return book;
    }

    private Book update(Book book) {
        try {
            return em.merge(book);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(NO_ENTRIES_UPDATED_MESSAGE);
        }

    }
}
