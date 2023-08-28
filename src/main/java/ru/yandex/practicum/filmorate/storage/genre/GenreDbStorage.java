package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlQuery = "select genre_id, name from genres order by genre_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "select genre_id, name from genres where genre_id = ?";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        if (genres.size() == 0) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден.");
        } else {
            return genres.get(0);
        }
    }

    @Override
    public boolean delete(Integer id) {
        getGenreById(id);

        String sqlQueryDeleteInFilmGenres = "delete from film_genres where genre_id = ?";
        jdbcTemplate.update(sqlQueryDeleteInFilmGenres, id);

        String sqlQueryDeleteGenre = "delete from genres where genre_id = ?";
        return jdbcTemplate.update(sqlQueryDeleteGenre, id) > 0;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
            .id(resultSet.getInt("genre_id"))
            .name(resultSet.getString("name"))
            .build();
    }
}