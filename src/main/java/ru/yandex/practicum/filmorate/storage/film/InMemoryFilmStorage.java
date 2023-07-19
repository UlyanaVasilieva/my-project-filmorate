package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static int filmId = 0;
    @Autowired
    private FilmValidator validator;

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        if (films.containsValue(film)) {
            throw new FilmValidationException("Фильм " + film.getName() + " уже добавлен.");
        }

        validator.validateFilmData(film);
        film.setId(++filmId);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            validator.validateFilmData(film);
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
}