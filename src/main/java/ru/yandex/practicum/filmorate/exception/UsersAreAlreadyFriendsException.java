package ru.yandex.practicum.filmorate.exception;

public class UsersAreAlreadyFriendsException extends AlreadyDoneException {
    public UsersAreAlreadyFriendsException(String message) {
        super(message);
    }
}