package ru.yandex.practicum.filmorate.exception;

public class FilmValidationException extends ValidationException {
    public FilmValidationException(String message) {
        super(message);
    }
}