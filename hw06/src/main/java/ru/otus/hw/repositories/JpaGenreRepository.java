package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@AllArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "repository-type", havingValue = "jpa")
public class JpaGenreRepository implements GenreRepository {
    private final EntityManager em;

    @Override
    public List<Genre> findAll() {
        return em.createQuery("select g from Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return em.createQuery("select g from Genre g where g.id in (:ids)", Genre.class).setParameter("ids", ids)
                .getResultList();
    }
}
