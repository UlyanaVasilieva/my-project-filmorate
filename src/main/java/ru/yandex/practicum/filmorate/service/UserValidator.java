package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
public class UserValidator {
    public void validateUserData(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Заменяем пустое имя на логин.");
            user.setName(user.getLogin());
        }

        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин не может содержать пробелы.");
        }
    }
}