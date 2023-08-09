package ru.yandex.practicum.filmorate.exception;

public class RatingNotFoundException extends NotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}