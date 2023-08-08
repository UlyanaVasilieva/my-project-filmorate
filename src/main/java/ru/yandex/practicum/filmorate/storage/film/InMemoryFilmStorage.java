package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Qualifier("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static int filmId = 0;

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        if (films.containsValue(film)) {
            throw new FilmValidationException("Фильм " + film.getName() + " уже добавлен.");
        }

        film.setId(++filmId);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new FilmNotFoundException("Фильм не найден.");
        }

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }

        return films.get(id);
    }

    @Override
    public boolean delete(Integer id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }

        films.remove(id);
        return true;
    }

    @Override
    public Film addLike(Integer filmId, Long userId) {
        Film film = this.getFilmById(filmId);
        initLikesIfNull(film);
        film.getLikes().add(userId);

        return film;
    }

    @Override
    public Film removeLike(Integer filmId, Long userId) {
        Film film = this.getFilmById(filmId);

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

    private void initLikesIfNull(Film film) {
        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
        }

        film.setLikes(likes);
    }
}