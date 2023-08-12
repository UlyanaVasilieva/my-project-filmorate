package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private static Film film;
    private static Integer filmId = 1;
    private static Long userId = 1L;

    @BeforeEach
    void setUp() {
        film = Film.builder()
            .id(++filmId)
            .name("test")
            .mpa(new MpaRating(1, "G"))
            .likes(new HashSet<>())
            .duration(120)
            .releaseDate(LocalDate.of(2000, 11, 11))
            .description("test-super-film")
            .genres(new HashSet<>())
            .build();

        filmDbStorage.create(film);
    }

    @AfterEach
    void afterEach() {
        for (Film storageFilm : filmDbStorage.getFilms()) {
            filmDbStorage.deleteFilmById(storageFilm.getId());
        }
        filmDbStorage.getFilms().clear();
    }

    @Test
    void create() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.create(film));

        assertThat(filmOptional)
            .isPresent();
    }

    @Test
    void addLike() {
        User user = new User(
            userId++,
            "test@mail.ru",
            "test",
            "test",
            LocalDate.of(2000, 11, 11),
            new ArrayList<>()
        );

        userDbStorage.create(user);
        filmDbStorage.addLike(film.getId(), user.getId());

        assertEquals(filmDbStorage.getFilmById(film.getId()).getLikes().size(), 1);
    }

    @Test
    void removeLike() {
        User user = new User(
            userId++,
            "test@mail.ru",
            "test",
            "test",
            LocalDate.of(2000, 11, 11),
            new ArrayList<>()
        );

        userDbStorage.create(user);
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.removeLike(film.getId(), user.getId());

        assertEquals(filmDbStorage.getFilmById(film.getId()).getLikes().size(), 0);
    }

    @Test
    void update() {
        Film updated = Film.builder()
            .id(film.getId())
            .name("updated")
            .mpa(new MpaRating(1, "name"))
            .likes(new HashSet<>())
            .duration(120)
            .releaseDate(LocalDate.of(2000, 11, 11))
            .description("test-super-film")
            .genres(new HashSet<>())
            .build();

        Optional<Film> filmOptional = Optional.of(filmDbStorage.update(updated));

        assertThat(filmOptional)
            .isPresent()
            .hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("name", "updated")
            );
    }

    @Test
    void getFilms() {
        Film film2 = Film.builder()
            .id(filmId++)
            .name("new")
            .mpa(new MpaRating(1, "name"))
            .likes(new HashSet<>())
            .duration(120)
            .releaseDate(LocalDate.of(2000, 11, 11))
            .description("test-super-film")
            .genres(new HashSet<>())
            .build();

        filmDbStorage.create(film2);
        Collection<Film> films = filmDbStorage.getFilms();

        assertEquals(films.size(), 2);
    }

    @Test
    void getFilmById() {
        Integer id = film.getId();
        Optional<Film> filmOptional = Optional.of(film);

        assertThat(filmOptional)
            .isPresent()
            .hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", id)
            );
    }
}