package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmValidator {
    public void validateFilmData(Film film) throws FilmValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }
}