package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("films")
            .usingGeneratedKeyColumns("film_id");

        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        insertFilmGenreInTable(film, filmId);

        return getFilmById(filmId);
    }

    @Override
    public Film update(Film film) {
        getFilmById(film.getId());

        String sqlQuery = "update films set " +
            "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "where film_id = ?";

        jdbcTemplate.update(
            sqlQuery,
            film.getName(),
            film.getDescription(),
            film.getReleaseDate(),
            film.getDuration(),
            film.getMpa().getId(),
            film.getId()
        );

        updateFilmGenres(film);
        updateFilmLikes(film);

        return getFilmById(film.getId());
    }

    @Override
    public Collection<Film> getFilms() {
        String sqlQuery =
            "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, mp.name as mpa_name " +
                "from films as f " +
                "left join mpa_rating as mp on f.rating_id = mp.rating_id ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery =
            "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, mp.name as mpa_name " +
                "from films as f " +
                "left join mpa_rating as mp on f.rating_id = mp.rating_id " +
                "where f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        if (films.size() == 0) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        } else {
            return films.get(0);
        }
    }

    @Override
    public boolean deleteFilmById(Integer id) {
        getFilmById(id);

        String sqlQueryDeleteLikes = "delete from likes where film_id = ?";
        jdbcTemplate.update(sqlQueryDeleteLikes, id);

        String sqlQueryDeleteGenres = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQueryDeleteGenres, id);

        String sqlQueryDeleteFilms = "delete from films where film_id = ?";
        return jdbcTemplate.update(sqlQueryDeleteFilms, id) > 0;
    }

    @Override
    public Film addLike(Integer filmId, Long userId) {
        Film film = this.getFilmById(filmId);
        initLikesIfNull(film);
        film.getLikes().add(userId);
        this.insertLikesInTable(userId, filmId);

        return film;
    }

    @Override
    public Film removeLike(Integer filmId, Long userId) {
        Film film = this.getFilmById(filmId);
        initLikesIfNull(film);

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            String sqlQueryDeleteLikes = "delete from likes where user_id = ?";
            jdbcTemplate.update(sqlQueryDeleteLikes, userId);
        } else {
            throw new UserNotFoundException("Лайк данного пользователя не найден.");
        }

        return film;
    }

    private void updateFilmGenres(Film film) {
        if (film.getGenres() != null) {
            String sqlQueryDeleteGenres = "delete from film_genres where film_id = ?";
            jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

            insertFilmGenreInTable(film, film.getId());
        }
    }

    private void insertFilmGenreInTable(Film film, Integer filmId) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "insert into film_genres (film_id, genre_id) values (?, ?)";
                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            }
        }
    }

    private void updateFilmLikes(Film film) {
        if (film.getLikes() != null) {
            String sqlQueryDeleteLikes = "delete from likes where film_id = ?";
            jdbcTemplate.update(sqlQueryDeleteLikes, film.getId());

            for (Long userId : film.getLikes()) {
                String sqlQueryAddLikes = "insert into likes (film_id, user_id) values (?, ?)";
                jdbcTemplate.update(sqlQueryAddLikes, film.getId(), userId);
            }
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
            .id(resultSet.getInt("film_id"))
            .name(resultSet.getString("name"))
            .description(resultSet.getString("description").trim())
            .releaseDate(resultSet.getDate("release_date").toLocalDate())
            .duration(resultSet.getInt("duration"))
            .mpa(createMpa(resultSet))
            .likes(getAllFilmLikes(resultSet))
            .genres(getAllFilmGenres(resultSet))
            .build();
    }

    private MpaRating createMpa(ResultSet resultSet) throws SQLException {
        return new MpaRating(
            resultSet.getInt("rating_id"),
            resultSet.getString("mpa_name").trim()
        );
    }

    private HashSet<Genre> getAllFilmGenres(ResultSet resultSet) throws SQLException {
        String sqlQueryGenre = "select g.genre_id, g.name " +
            "from film_genres as fg " +
            "inner join genres as g on fg.genre_id = g.genre_id " +
            "where fg.film_id = ?";

        return jdbcTemplate.query(
            sqlQueryGenre,
            this::getFilmGenre,
            resultSet.getInt("film_id")
        );
    }

    private HashSet<Genre> getFilmGenre(ResultSet resultSet) throws SQLException {
        HashSet<Genre> genres = new HashSet<>();

        while (resultSet.next()) {
            Integer genreId = resultSet.getInt("genre_id");
            String genreName = resultSet.getString("name");
            genres.add(new Genre(genreId, genreName));
        }

        return genres;
    }

    private Set<Long> getAllFilmLikes(ResultSet resultSet) throws SQLException {
        String sqlQueryLikes = "select like_id, film_id, user_id " +
            "from likes " +
            "where film_id = ?";
        List<Long> usersWhoLiked = jdbcTemplate.query(
            sqlQueryLikes,
            (resultSet1, rowNum) -> getUsersWhoLikedFilm(resultSet1), resultSet.getInt("film_id")
        );

        return new HashSet<>(usersWhoLiked);
    }

    private Long getUsersWhoLikedFilm(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("user_id");
    }

    private void insertLikesInTable(Long userId, Integer filmId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("likes")
            .usingGeneratedKeyColumns("like_id");

        Map<String, Object> values = new HashMap<>();
        values.put("film_id", filmId);
        values.put("user_id", userId);
        simpleJdbcInsert.executeAndReturnKey(values).intValue();
    }

    private void initLikesIfNull(Film film) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
        }

        film.setLikes(likes);
    }
}