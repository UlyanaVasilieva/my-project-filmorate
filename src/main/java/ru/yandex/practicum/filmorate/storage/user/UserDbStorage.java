package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("users")
            .usingGeneratedKeyColumns("user_id");

        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        return getUserById(userId);
    }

    @Override
    public User update(User user) {
        getUserById(user.getId());

        String sqlQuery = "update users set " +
            "email = ?, login = ?, name = ?, birthday = ?" +
            "where user_id = ?";

        jdbcTemplate.update(
            sqlQuery,
            user.getEmail(),
            user.getLogin(),
            user.getName(),
            user.getBirthday(),
            user.getId()
        );

        updateUserFriendships(user);

        return getUserById(user.getId());
    }

    @Override
    public Collection<User> getUsers() {
        String sqlQuery = "select user_id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "select user_id, email, login, name, birthday from users where user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        if (users.size() == 0) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        } else {
            return users.get(0);
        }
    }

    @Override
    public boolean deleteUserById(Long id) {
        getUserById(id);

        String sqlQueryDeleteLikes = "delete from likes where user_id = ?";
        jdbcTemplate.update(sqlQueryDeleteLikes, id);

        String sqlQueryDeleteFriends = "delete from user_friendships where sender_id = ? or recipient_id = ?";
        jdbcTemplate.update(sqlQueryDeleteFriends, id, id);

        String sqlQueryDeleteUsers = "delete from users where user_id = ?";
        return jdbcTemplate.update(sqlQueryDeleteUsers, id) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
            .id(resultSet.getLong("user_id"))
            .email(resultSet.getString("email"))
            .login(resultSet.getString("login"))
            .name(resultSet.getString("name"))
            .birthday(resultSet.getDate("birthday").toLocalDate())
            .friendships(getAllUserFriendships(resultSet))
            .build();
    }

    private List<Friendship> getAllUserFriendships(ResultSet resultSet) throws SQLException {
        String sqlQueryFriends = "select sender_id, recipient_id, is_friend from user_friendships " +
            "where sender_id = ? or (recipient_id = ? and is_friend = true)";

        return jdbcTemplate.query(
            sqlQueryFriends,
            (resultSet1, rowNum) ->
                getUserFriends(resultSet1),
            resultSet.getLong("user_id"),
            resultSet.getLong("user_id")
        );
    }

    private Friendship getUserFriends(ResultSet resultSet) throws SQLException {
        Long senderId = resultSet.getLong("sender_id");
        Long recipientId = resultSet.getLong("recipient_id");
        boolean isFriend = resultSet.getBoolean("is_friend");

        return new Friendship(senderId, recipientId, isFriend);
    }

    private void updateUserFriendships(User user) {
        if (user.getFriendships() != null) {
            String sqlQueryDeleteFriends = "delete from user_friendships where sender_id = ? or recipient_id = ?";
            jdbcTemplate.update(sqlQueryDeleteFriends, user.getId(), user.getId());

            for (Friendship friendship : user.getFriendships()) {
                String sqlQueryAddFriend = "insert into user_friendships(sender_id, recipient_id, is_friend) " +
                    "values (?, ?, ?)";
                jdbcTemplate.update(
                    sqlQueryAddFriend,
                    friendship.getSenderId(),
                    friendship.getRecipientId(),
                    friendship.isFriend()
                );
            }
        }
    }
}