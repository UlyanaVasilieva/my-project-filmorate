package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAll() {
        assertEquals(genreDbStorage.getAllGenres().size(), 6);
    }

    @Test
    void getById() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreDbStorage.getGenreById(1));

        assertThat(genreOptional)
            .isPresent()
            .hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("name", "Комедия")
            );
    }
}