package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class UserService {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int userId = 0;
    @Autowired
    private UserValidator validator;

    public User create(User user) {
        validator.validateUserData(user);
        user.setId(++userId);
        users.put(user.getId(), user);
        log.info("Зарегистрирован новый пользователь: " + user.getName());

        return user;
    }

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            validator.validateUserData(user);
            users.put(user.getId(), user);
            log.info("Обновлена информация пользователя " + user.getName());
        } else {
            throw new UserValidationException("Пользователь не найден.");
        }

        return user;
    }

    public Collection<User> getUsers() {
        return users.values();
    }
}