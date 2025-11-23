package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query("SELECT id, full_name FROM authors",
                new JdbcAuthorRepository.AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        List<Author> authors =
                namedParameterJdbcOperations.query("SELECT id, full_name FROM authors WHERE id = :authorId",
                        Map.of("authorId", id), new JdbcAuthorRepository.AuthorRowMapper());
        Author result = authors.isEmpty() ? null : authors.get(0);
        return Optional.ofNullable(result);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("id");
            String fullName = rs.getString("full_name");
            return new Author(id, fullName);
        }
    }
}
