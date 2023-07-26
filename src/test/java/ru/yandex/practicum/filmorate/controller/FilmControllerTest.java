package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FilmService filmService;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
            .id(1)
            .name("test name")
            .description("test description")
            .duration(60)
            .releaseDate(LocalDate.of(2001, 12, 12))
            .build();
    }

    @Test
    void addFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testFilm))
            )
            .andExpect(status().isOk());

        verify(filmService).create(any(Film.class));
    }

    @Test
    void addFilmWithEmptyName() throws Exception {
        testFilm.setName("");

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm))
            )
            .andExpect(status().is5xxServerError());
    }

    @Test
    void addFilmWithOverDescription() throws Exception {
        testFilm.setDescription("Невероятно интересный фильм, поднимающий животрепещущую тему современного мира, " +
            "который не может быть добавлен в написанное приложение из-за избыточного количества символов: " +
            "а именно, в количестве более двухсот.");

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm))
            )
            .andExpect(status().is5xxServerError());
    }

    @Test
    void addFilmWithZeroDuration() throws Exception {
        testFilm.setDuration(0);

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm))
            )
            .andExpect(status().is5xxServerError());
    }

    @Test
    void addFilmWithOldDate() throws Exception {
        testFilm.setReleaseDate(LocalDate.of(1890, 12, 21));

        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(testFilm))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
        Assertions.assertEquals(new ArrayList<>(), filmService.getFilms());
    }

    @Test
    void addFilmWithNegativeDuration() throws Exception {
        testFilm.setDuration(-100);

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFilm))
            )
            .andExpect(status().is5xxServerError());
    }
}