package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getAll() {
        assertEquals(mpaDbStorage.getAllRatings().size(), 5);
    }

    @Test
    void getById() {
        Optional<MpaRating> mpaOptional = Optional.ofNullable(mpaDbStorage.getMpaById(1));

        assertThat(mpaOptional)
            .isPresent()
            .hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "G")
            );
    }
}