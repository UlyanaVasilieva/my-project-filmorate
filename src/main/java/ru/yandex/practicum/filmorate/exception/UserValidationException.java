package ru.yandex.practicum.filmorate.exception;

public class UserValidationException extends ValidationException {
    public UserValidationException(String message) {
        super(message);
    }
}