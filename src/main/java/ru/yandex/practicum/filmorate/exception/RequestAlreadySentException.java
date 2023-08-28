package ru.yandex.practicum.filmorate.exception;

public class RequestAlreadySentException extends AlreadyDoneException {
    public RequestAlreadySentException(String message) {
        super(message);
    }
}