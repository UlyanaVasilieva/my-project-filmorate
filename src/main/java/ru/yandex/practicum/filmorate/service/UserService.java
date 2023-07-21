package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator validator;

    public User create(User user) {
        validator.validateUserData(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validator.validateUserData(user);
        return userStorage.update(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        if (friendId <= 0) {
            throw new UserValidationException("id должно быть положительным.");
        }

        initFriendsIfNull(userId);
        initFriendsIfNull(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return userStorage.getUserById(friendId);
    }

    public User removeFriend(Long userId, Long friendId) {
        initFriendsIfNull(userId);
        initFriendsIfNull(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());

        return user;
    }

    public List<User> getFriends(Long id) {
        for (User user : userStorage.getUsers()) {
            initFriendsIfNull(user.getId());
        }

        return userStorage.getUsers().stream()
            .filter(user -> user.getFriends().contains(id))
            .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        initFriendsIfNull(id);
        initFriendsIfNull(otherId);

        List<User> commonFriends = new ArrayList<>();
        Set<Long> userFriends = userStorage.getUserById(id).getFriends();
        Set<Long> otherUserFriends = userStorage.getUserById(otherId).getFriends();

        for (Long userFriend : userFriends) {
            if (otherUserFriends.contains(userFriend)) {
                commonFriends.add(userStorage.getUserById(userFriend));
            }
        }

        return commonFriends;
    }

    private void initFriendsIfNull(Long userId) {
        User user = userStorage.getUserById(userId);

        Set<Long> friends = user.getFriends();
        if (friends == null) {
            friends = new HashSet<>();
        }

        user.setFriends(friends);
    }
}