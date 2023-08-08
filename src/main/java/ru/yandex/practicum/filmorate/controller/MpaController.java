package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    public Collection<MpaRating> getAllRatings() {
        log.info("Пришел запрос на получение списка всех рейтингов.");
        return mpaStorage.getAllRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getRatingById(@PathVariable Integer id) {
        log.info("Пришел запрос на получение рейтинга с id " + id);
        return mpaStorage.getMpaById(id);
    }
}