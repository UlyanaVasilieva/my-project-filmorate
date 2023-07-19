package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private static int userId = 0;
    @Autowired
    private UserValidator validator;

    @Override
    public User create(User user) {
        if (users.containsValue(user)) {
            throw new UserAlreadyExistException("Пользователь " + user.getName() + " уже существует.");
        }

        validator.validateUserData(user);
        user.setId((long) ++userId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            validator.validateUserData(user);
            users.put(user.getId(), user);
        } else {
            throw new UserNotFoundException("Пользователь " + user.getName() + " не найден.");
        }

        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }

        return users.get(id);
    }
}