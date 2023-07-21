package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final static int DEFAULT_COUNT_VALUE = 10;
    private final FilmValidator validator;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmValidator validator) {
        this.filmStorage = filmStorage;
        this.validator = validator;
    }

    public Film create(Film film) {
        validator.validateFilmData(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validator.validateFilmData(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(Integer filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        initLikesIfNull(film);
        film.getLikes().add(userId);

        return film;
    }

    public Film removeLike(Integer filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (userId <= 0) {
            throw new UserNotFoundException("Лайк данного пользователя не найден.");
        }

        initLikesIfNull(film);

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new UserNotFoundException("Лайк данного пользователя не найден.");
        }

        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        count = checkPopularFilmCount(count);

        filmStorage.getFilms().forEach(this::initLikesIfNull);

        return filmStorage.getFilms().stream()
            .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
            .limit(count)
            .collect(Collectors.toList());
    }

    public void initLikesIfNull(Film film) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
        }

        film.setLikes(likes);
    }

    private Integer checkPopularFilmCount(Integer count) {
        if (count == null || count == 0) {
            count = DEFAULT_COUNT_VALUE;
        }

        return count;
    }
}