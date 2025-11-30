package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SimplePropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "repository-type", havingValue = "jdbc")
public class JdbcBookRepository implements BookRepository {
    private static final String NO_ENTRIES_UPDATED_MESSAGE = "Ни одной записи не обновлено";

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        Book result = namedParameterJdbcOperations.query("""
                SELECT b.id AS b_id, b.title AS b_title, b.author_id AS a_id, a.full_name AS a_full_name,\
                       g.id AS g_id, g.name AS g_name
                  FROM books AS b
                 INNER JOIN authors AS a ON b.author_id = a.id
                 INNER JOIN books_genres bg ON b.id = bg.book_id
                 INNER JOIN genres g ON bg.genre_id = g.id
                 WHERE b.id = :bookId""", Map.of("bookId", id), new BookResultSetExtractor());

        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
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
        namedParameterJdbcOperations.update("DELETE FROM books WHERE id = :bookId", Map.of("bookId", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        //@formatter:off
        return namedParameterJdbcOperations.query(
                        "SELECT b.id, b.title, b.author_id, a.full_name\n" +
                        "  FROM books b\n" +
                        "       JOIN authors a ON b.author_id = a.id", new BookRowMapper());
        //@formatter:on
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        //@formatter:off
        return namedParameterJdbcOperations.query(
                "SELECT book_id, genre_id FROM books_genres",
                new SimplePropertyRowMapper<>(BookGenreRelation.class));
        //@formatter:on
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres, List<BookGenreRelation> relations) {
        Map<Long, Genre> genreById = genres.stream().collect(Collectors.toMap(Genre::getId, g -> g));
        for (Book book : booksWithoutGenres) {
            List<Genre> bookGenres = relations.stream().filter(relation -> relation.bookId == book.getId())
                    .map(BookGenreRelation::genreId).map(genreById::get).collect(Collectors.toList());
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params =
                new MapSqlParameterSource(Map.of("title", book.getTitle(), "authorId", book.getAuthor().getId()));
        //@formatter:off
        namedParameterJdbcOperations.update(
                "INSERT INTO books (title, author_id) VALUES (:title, :authorId)", params,
                keyHolder, new String[]{"id"});
        //@formatter:on

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        //@formatter:off
        var affectedRowCount = namedParameterJdbcOperations.update(
                "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id",
                Map.of("id", book.getId(), "title", book.getTitle(), "authorId", book.getAuthor().getId()));
        //@formatter:on

        if (affectedRowCount == 0) {
            throw new EntityNotFoundException(NO_ENTRIES_UPDATED_MESSAGE);
        }
        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var bookGenreRelations =
                book.getGenres().stream().map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                        .collect(Collectors.toList());

        //@formatter:off
        namedParameterJdbcOperations.batchUpdate(
                "INSERT INTO books_genres (book_id, genre_id) " +
                "VALUES (:bookId, :genreId)",
                SqlParameterSourceUtils.createBatch(bookGenreRelations));
        //@formatter:on
    }

    private void removeGenresRelationsFor(Book book) {
        namedParameterJdbcOperations.update("DELETE FROM books_genres WHERE book_id = :bookId",
                Map.of("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            int authorId = rs.getInt("author_id");
            String fullName = rs.getString("full_name");
            var author = new Author(authorId, fullName);
            return new Book(id, title, author, null);
        }
    }


    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                long id = rs.getBigDecimal("b_id").longValue();
                String title = rs.getString("b_title");
                var author = extractAuthor(rs);
                var genres = extractGenres(rs);

                return new Book(id, title, author, genres);
            }
            return null;
        }

        private Author extractAuthor(ResultSet rs) throws SQLException {
            long authorId = rs.getLong("a_id");
            String fullName = rs.getString("a_full_name");
            return new Author(authorId, fullName);
        }

        private List<Genre> extractGenres(ResultSet rs) throws SQLException {
            List<Genre> genres = new ArrayList<>();
            do {
                var genre = extractGenre(rs);
                genres.add(genre);
            } while (rs.next());
            return genres;
        }

        private Genre extractGenre(ResultSet rs) throws SQLException {
            long id = rs.getLong("g_id");
            String name = rs.getString("g_name");
            return new Genre(id, name);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
