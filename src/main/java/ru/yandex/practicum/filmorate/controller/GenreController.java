package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Пришел запрос на получение списка всех жанров.");
        return genreStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("Пришел запрос на получение жанра с id " + id);
        return genreStorage.getGenreById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteGenreById(@PathVariable Integer id) {
        log.info("Пришел запрос на удаление жанра с id " + id);
        return genreStorage.delete(id);
    }
}