package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@AllArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "repository-type", havingValue = "jdbc")
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query("SELECT id, name FROM genres",
                new JdbcGenreRepository.GnreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return namedParameterJdbcOperations.query(
                "SELECT id, name FROM genres WHERE id IN (:genreIds)", Map.of("genreIds", ids),
                new JdbcGenreRepository.GnreRowMapper());
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("id");
            String fullName = rs.getString("name");
            return new Genre(id, fullName);
        }
    }
}
