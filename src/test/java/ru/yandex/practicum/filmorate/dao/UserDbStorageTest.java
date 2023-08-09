package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static User user;
    private static Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User(
            ++userId,
            "test@mail.ru",
            "test",
            "test",
            LocalDate.of(2000, 11, 11),
            new ArrayList<>()
        );

        userStorage.create(user);
    }

    @AfterEach
    void afterEach() {
        for (User storageUser : userStorage.getUsers()) {
            userStorage.delete(storageUser.getId());
        }
        userStorage.getUsers().clear();
    }

    @Test
    public void getUserById() {
        Long id = user.getId();
        Optional<User> userOptional = Optional.of(user);

        assertThat(userOptional)
            .isPresent()
            .hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", id)
            );
    }

    @Test
    public void createUser() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));

        assertThat(userOptional)
            .isPresent();
    }

    @Test
    public void getUsers() {
        User user2 = new User(
            2L,
            "test2@mail.ru",
            "test2",
            "test2",
            LocalDate.of(2001, 11, 11),
            new ArrayList<>()
        );

        userStorage.create(user2);
        Collection<User> users = userStorage.getUsers();

        assertEquals(users.size(), 2);
    }

    @Test
    public void updateUser() {
        User updated = new User(
            1L,
            "update@mail.ru",
            "update",
            "update",
            LocalDate.of(2000, 11, 11),
            new ArrayList<>()
        );

        Optional<User> userOptional = Optional.ofNullable(userStorage.update(updated));

        assertThat(userOptional)
            .isPresent()
            .hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "update")
            );
    }
}