package com.lk.swapiclone.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record FilmCreateRequest(
    @NotBlank @Size(max = 255) String title,
    @PositiveOrZero Integer episodeId,
    @NotBlank @Size(max = 10_000) String openingCrawl,
    @NotBlank @Size(max = 255) String director,
    @NotBlank @Size(max = 255) String producer,
    LocalDate releaseDate,
    List<@NotNull @Positive Long> characters,
    List<@NotNull @Positive Long> planets,
    List<@NotNull @Positive Long> species,
    List<@NotNull @Positive Long> starships,
    List<@NotNull @Positive Long> vehicles
) {}
