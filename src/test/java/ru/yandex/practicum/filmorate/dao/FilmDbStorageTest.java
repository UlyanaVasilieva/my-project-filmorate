package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private static Film film;

    @BeforeAll
    static void setUp() {
        film = Film.builder()
            .id(1)
            .name("test")
            .mpa(new MpaRating(1, "name"))
            .likes(new HashSet<>())
            .duration(120)
            .releaseDate(LocalDate.of(2000, 11, 11))
            .description("test-super-film")
            .genres(new HashSet<>())
            .build();
    }

    @AfterEach
    void afterEach() {
        filmDbStorage.getFilms().clear();
    }

    @Test
    void create() {
        filmDbStorage.create(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.create(film));

        assertThat(filmOptional)
            .isPresent();
    }

    @Test
    void update() {
    }

    @Test
    void getFilms() {
    }

    @Test
    void getFilmById() {

    }

    @Test
    void delete() {
    }

    @Test
    void addLike() {

    }

    @Test
    void removeLike() {

    }
}
