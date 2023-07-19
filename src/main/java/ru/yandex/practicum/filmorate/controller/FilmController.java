package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Пришел запрос на получение списка всех фильмов.");
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Пришел запрос на получение фильма по id " + id);
        return filmStorage.getFilmById(id);
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) {
        log.info("Пришел запрос на создание фильма с названием " + film.getName());
        return filmStorage.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Пришел запрос на обновление фильма с id " + film.getId());
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable Integer id, @PathVariable Long userId) {
        log.info(
            "Пришел запрос на добавление лайка пользователя фильму " +
                filmStorage.getFilmById(id).getName()
        );

        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id, @PathVariable Long userId) {
        log.info(
            "Пришел запрос на удаление лайка пользователя с фильма " +
                filmStorage.getFilmById(id)
        );

        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Пришел запрос на получение списка самых популярных фильмов.");
        return filmService.getMostPopularFilms(count);
    }
}