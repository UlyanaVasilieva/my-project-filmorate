package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Пришел запрос на получение списка всех пользователей.");
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Пришел запрос на получение пользователя с id " + id);
        return userStorage.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Пришел запрос на создание пользователя " + user.getName());
        return userStorage.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Пришел запрос на обновление пользователя с id" + user.getId());
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос на добавление друга.");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пришел запрос на удаление друга " + userStorage.getUserById(friendId));
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info(
            "Пришел запрос на получение списка всех друзей пользователя " + userStorage.getUserById(id)
        );

        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info(
            "Пришел запрос на получение списка общих друзей пользователя " +
                userStorage.getUserById(id).getName() + " и пользователя " +
                userStorage.getUserById(otherId).getName() + "."
        );

        return userService.getCommonFriends(id, otherId);
    }
}