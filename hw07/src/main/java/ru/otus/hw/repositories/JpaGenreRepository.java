package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface JpaGenreRepository extends JpaRepository<Genre, Long>, GenreRepository {

    List<Genre> findByIdIn(Set<Long> ids);
}
