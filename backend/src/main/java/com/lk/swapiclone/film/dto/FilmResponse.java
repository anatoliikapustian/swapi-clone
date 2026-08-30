package com.lk.swapiclone.film.dto;

import java.util.List;

public record FilmResponse(
    Long id,
    String title,
    Integer episodeId,
    String openingCrawl,
    String director,
    String producer,
    String releaseDate,
    List<String> characters,
    List<String> planets,
    List<String> species,
    List<String> starships,
    List<String> vehicles,
    String url,
    String created,
    String edited
) {}
