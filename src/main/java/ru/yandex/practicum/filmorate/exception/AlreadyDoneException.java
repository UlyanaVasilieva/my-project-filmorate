package ru.yandex.practicum.filmorate.exception;

public class AlreadyDoneException extends RuntimeException {
    public AlreadyDoneException(String message) {
        super(message);
    }
}
