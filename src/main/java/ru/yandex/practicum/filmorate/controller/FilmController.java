package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Пришел запрос на получение списка всех фильмов.");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Пришел запрос на получение фильма по id " + id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFilmById(@PathVariable Integer id) {
        log.info("Пришел запрос на удаление фильма с id " + id);
        return filmService.deleteFilmById(id);
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) {
        log.info("Пришел запрос на создание фильма с названием " + film.getName());
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Пришел запрос на обновление фильма с id " + film.getId());
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable Integer id, @PathVariable Long userId) {
        log.info(
            "Пришел запрос на добавление лайка пользователя фильму " +
                filmService.getFilmById(id).getName()
        );

        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id, @PathVariable Long userId) {
        log.info(
            "Пришел запрос на удаление лайка пользователя с фильма " +
                filmService.getFilmById(id)
        );

        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Пришел запрос на получение списка самых популярных фильмов.");
        return filmService.getMostPopularFilms(count);
    }
}