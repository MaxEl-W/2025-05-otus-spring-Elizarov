package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JpaAuthorRepository implements AuthorRepository {
    private final EntityManager em;

    @Override
    public List<Author> findAll() {
        return em.createQuery("select a from Author a", Author.class).getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        Author result = em.find(Author.class, id);
        return Optional.ofNullable(result);
    }
}
