package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public Collection<MpaRating> getAllRatings() {
        String sqlQuery = "select * from mpa_rating order by rating_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public MpaRating getMpaById(Integer id) {
        String sqlQuery = "select * from mpa_rating where rating_id = ?";

        List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, this::mapRowToRating, id);
        if (ratings.size() == 0) {
            throw new RatingNotFoundException("Рейтинг с id " + id + " не найден.");
        } else {
            return ratings.get(0);
        }
    }

    @Override
    public boolean delete(Integer id) {
        getMpaById(id);

        String sqlQueryDeleteRatingInFilms = "update films set rating_id = null where rating_id = ?";
        jdbcTemplate.update(sqlQueryDeleteRatingInFilms, id);

        String sqlQueryDeleteMpa = "delete from mpa_rating where rating_id = ?";
        return jdbcTemplate.update(sqlQueryDeleteMpa, id) > 0;
    }

    private MpaRating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
            .id(resultSet.getInt("rating_id"))
            .name(resultSet.getString("name").trim())
            .build();
    }
}