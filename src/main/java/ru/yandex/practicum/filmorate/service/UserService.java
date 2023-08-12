package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RequestAlreadySentException;
import ru.yandex.practicum.filmorate.exception.UsersAreAlreadyFriendsException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator validator;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserValidator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

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

    public boolean deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        initFriendsIfNull(user);
        initFriendsIfNull(getUserById(friendId));

        User friend = updateFriendship(userId, friendId);
        if (friend != null) {
            return update(friend);
        }

        user.getFriendships().add(new Friendship(userId, friendId, false));

        return update(user);
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        initFriendsIfNull(user);
        initFriendsIfNull(friend);

        user.getFriendships().remove(getFriendship(friend, user));
        friend.getFriendships().remove(getFriendship(user, friend));

        return update(user);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUserById(id);
        initFriendsIfNull(user);

        ArrayList<User> friends = new ArrayList<>();
        for (Friendship friendship : user.getFriendships()) {
            if (Objects.equals(id, friendship.getRecipientId())) {
                friends.add(getUserById(friendship.getSenderId()));
            } else {
                friends.add(getUserById(friendship.getRecipientId()));
            }
        }

        return friends;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id);
        User otherUser = userStorage.getUserById(otherId);
        initFriendsIfNull(user);
        initFriendsIfNull(otherUser);

        List<User> userFriends = getUserFriendshipRequests(user);
        List<User> otherUserFriends = getUserFriendshipRequests(otherUser);

        return fillListWithCommonFriends(userFriends, otherUserFriends);
    }

    private List<User> getUserFriendshipRequests(User user) {
        List<User> userFriendshipRequests = new ArrayList<>();

        for (Friendship friendship : user.getFriendships()) {
            if (Objects.equals(user.getId(), friendship.getRecipientId())) {
                userFriendshipRequests.add(getUserById(friendship.getSenderId()));
            } else {
                userFriendshipRequests.add(getUserById(friendship.getRecipientId()));
            }
        }

        return userFriendshipRequests;
    }

    private List<User> fillListWithCommonFriends(List<User> userFriends, List<User> otherUserFriends) {
        List<User> commonFriends = new ArrayList<>();

        for (User userFriend : userFriends) {
            if (otherUserFriends.contains(userFriend)) {
                commonFriends.add(userFriend);
            }
        }

        return commonFriends;
    }

    private void initFriendsIfNull(User user) {
        if (user.getFriendships() == null) {
            user.setFriendships(new ArrayList<>());
        }
    }

    private User updateFriendship(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        for (Friendship friendship : user.getFriendships()) {
            if (didFriendSentRequest(userId, friendId, friendship)) {
                throw new RequestAlreadySentException("Заявка в друзья уже отправлена.");
            }

            if (didFriendSentRequest(friendId, userId, friendship)) {
                if (friendship.isFriend()) {
                    throw new UsersAreAlreadyFriendsException("Пользователи уже являются друзьями.");
                }

                friendship.setFriend(true);
                Objects.requireNonNull(getFriendship(friend, user)).setFriend(true);

                return friend;
            }
        }

        return null;
    }

    private boolean didFriendSentRequest(Long userId, Long friendId, Friendship friendship) {
        return Objects.equals(friendship.getRecipientId(), friendId)
            && Objects.equals(friendship.getSenderId(), userId);
    }

    private Friendship getFriendship(User user, User friend) {
        for (Friendship friendship : user.getFriendships()) {
            boolean isThereFriendId = Objects.equals(friendship.getRecipientId(), friend.getId())
                || Objects.equals(friendship.getSenderId(), friend.getId());

            if (isThereFriendId) {
                return friendship;
            }
        }

        return null;
    }
}